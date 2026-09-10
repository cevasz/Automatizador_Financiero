package com.finanzas.automatica.service

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.domain.enrichment.EnrichmentPipeline
import com.finanzas.automatica.data.repository.CaptureLogRepository
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.parser.CaptureDiagnosis
import com.finanzas.automatica.domain.parser.ParserRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

class NotificationCaptureService : NotificationListenerService() {

    private val TAG = "NotificationCapture"
    private val parserRegistry = ParserRegistry.createDefault()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var enrichmentPipeline: EnrichmentPipeline? = null
    private var captureLog: CaptureLogRepository? = null

    override fun onCreate() {
        super.onCreate()
        val database = FinanzasDatabase.getInstance(this)
        enrichmentPipeline = EnrichmentPipeline(database)
        captureLog = CaptureLogRepository(database)
        Log.i(TAG, "NotificationCaptureService creado")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        handle(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // No necesitamos hacer nada especial aquí
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i(TAG, "Listener conectado - listo para capturar notificaciones")

        // El sistema desconecta y reconecta el listener por su cuenta (actualizaciones de
        // la app, memoria baja, reinicio). Todo lo que llegue mientras está desconectado
        // nunca dispara onNotificationPosted y se pierde -- esa es una de las razones por
        // las que "a veces" un movimiento no quedaba registrado. Al reconectar se revisan
        // las notificaciones que siguen en la barra: como se fechan con su `postTime`
        // real, las que ya se guardaron las descarta la detección de duplicados.
        val pending = try {
            activeNotifications
        } catch (t: Throwable) {
            Log.w(TAG, "No se pudieron leer las notificaciones activas", t)
            null
        }
        pending?.forEach { handle(it) }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.w(TAG, "Listener desconectado")
    }

    private fun handle(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val text = extractNotificationText(sbn.notification)
        if (text.isNullOrBlank()) return

        val diagnosis = parserRegistry.diagnose(packageName, text)

        // Lo que no es del banco se descarta aquí mismo, sin dejar rastro en ninguna
        // parte: el registro de diagnóstico solo puede ver avisos bancarios (ver
        // CaptureRejection.loggable).
        if (!diagnosis.loggable) return

        Log.d(TAG, "Notificación de $packageName: $text")

        val occurredAt = Instant.ofEpochMilli(sbn.postTime)
        scope.launch {
            processNotification(packageName, text, occurredAt, diagnosis)
        }
    }

    /**
     * Arma el texto completo de la notificación.
     *
     * Punto crítico: los extras se leen con `getCharSequence`, NO con `getString`. Un
     * banco que resalta el monto en negrita (o cualquier formato) guarda ahí un
     * `SpannableString`, y `Bundle.getString()` devuelve **null** para todo CharSequence
     * que no sea exactamente un String: la notificación llegaba vacía y se descartaba en
     * silencio. Eso explica que la misma app funcionara unas veces sí y otras no según el
     * tipo de mensaje.
     *
     * Se recogen además los estilos que parten el mensaje en varias piezas (InboxStyle
     * con `EXTRA_TEXT_LINES` y MessagingStyle con `EXTRA_MESSAGES`), porque en esos casos
     * el monto suele estar en una línea distinta al título.
     */
    private fun extractNotificationText(notification: Notification?): String? {
        val bundle = notification?.extras ?: return null

        val pieces = mutableListOf<String?>()
        SINGLE_TEXT_EXTRAS.forEach { key -> pieces += bundle.charSequence(key) }
        bundle.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
            ?.forEach { line -> pieces += line?.toString() }
        messagingLines(bundle).forEach { pieces += it }
        // `tickerText` es el texto plano que algunas apps siguen enviando cuando el resto
        // del contenido llega solo como vista personalizada.
        pieces += notification.tickerText?.toString()

        // Los mismos datos vienen repetidos entre extras (title dentro de bigText, por
        // ejemplo). Repetirlos hace que las reglas de monto vean el mismo valor dos veces.
        val unique = LinkedHashSet<String>()
        pieces.forEach { piece ->
            val value = piece?.trim().orEmpty()
            if (value.isNotEmpty()) unique += value
        }
        return unique.joinToString(" ").ifBlank { null }
    }

    private fun Bundle.charSequence(key: String): String? = try {
        getCharSequence(key)?.toString()
    } catch (t: Throwable) {
        null
    }

    @Suppress("DEPRECATION")
    private fun messagingLines(bundle: Bundle): List<String> {
        val messages = try {
            bundle.getParcelableArray(Notification.EXTRA_MESSAGES)
        } catch (t: Throwable) {
            null
        } ?: return emptyList()

        return messages.mapNotNull { message ->
            (message as? Bundle)?.charSequence("text")
        }
    }

    private suspend fun processNotification(
        packageName: String,
        text: String,
        occurredAt: Instant,
        diagnosis: CaptureDiagnosis
    ) {
        // scope = CoroutineScope(Dispatchers.IO) sin manejador de excepciones propio: si
        // algo aca (el parser, no solo EnrichmentPipeline) lanzara algo inesperado, sin
        // este try/catch tumbaria el proceso cada vez que llega una notificacion
        // bancaria -- la app quedaria crasheando en bucle en segundo plano.
        try {
            val parser = diagnosis.parser
            if (parser == null) {
                // Se descartó por reglas: queda anotado con el motivo para que el usuario
                // pueda verlo en Diagnóstico de captura en vez de perderlo en logcat.
                Log.w(TAG, "Notificación ignorada de $packageName: ${diagnosis.rejection?.label}")
                captureLog?.logIgnored(packageName, text, diagnosis)
                return
            }

            when (val result = parser.parse(text, occurredAt)) {
                is ParseResult.Success -> {
                    val rawMovement = result.movement
                    Log.i(TAG, "Parseado: ${rawMovement.type} ${rawMovement.amount} ${rawMovement.counterpartyRaw} (${rawMovement.bankEntity})")

                    // Pasar al pipeline de enriquecimiento y guardado
                    enrichmentPipeline?.process(rawMovement)
                    captureLog?.logCaptured(packageName, text, rawMovement)
                }
                is ParseResult.Failure -> {
                    Log.w(TAG, "Error parseando notificación de $packageName: ${result.error}")
                    Log.w(TAG, "Texto original: ${result.rawText}")
                    captureLog?.logFailed(packageName, text, parser.bankEntity.name, result.error)
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Fallo inesperado procesando notificación de $packageName", t)
        }
    }

    private companion object {
        val SINGLE_TEXT_EXTRAS = listOf(
            Notification.EXTRA_TITLE,
            Notification.EXTRA_TITLE_BIG,
            Notification.EXTRA_TEXT,
            Notification.EXTRA_BIG_TEXT,
            Notification.EXTRA_SUB_TEXT,
            Notification.EXTRA_SUMMARY_TEXT,
            Notification.EXTRA_INFO_TEXT
        )
    }
}
