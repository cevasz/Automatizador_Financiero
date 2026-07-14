package com.finanzas.automatica.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.content.ContextCompat
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.domain.enrichment.EnrichmentPipeline
import com.finanzas.automatica.domain.enrichment.MovementRepository
import com.finanzas.automatica.domain.model.MovementSource
import com.finanzas.automatica.domain.parser.ParserRegistry
import com.finanzas.automatica.domain.parser.ParseResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

class NotificationCaptureService : NotificationListenerService() {

    private val TAG = "NotificationCapture"
    private val parserRegistry = ParserRegistry.createDefault()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var enrichmentPipeline: EnrichmentPipeline? = null

    override fun onCreate() {
        super.onCreate()
        val database = FinanzasDatabase.getInstance(this)
        enrichmentPipeline = EnrichmentPipeline(database)
        Log.i(TAG, "NotificationCaptureService creado")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val notification = sbn.notification

        // Verificar si es de un banco soportado
        if (!parserRegistry.getSupportedPackages().contains(packageName)) {
            return
        }

        // Extraer texto de la notificación
        val text = extractNotificationText(notification)
        if (text.isNullOrBlank()) {
            return
        }

        Log.d(TAG, "Notificación de $packageName: $text")

        // Procesar en background
        scope.launch {
            processNotification(packageName, text)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // No necesitamos hacer nada especial aquí
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i(TAG, "Listener conectado - listo para capturar notificaciones")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.w(TAG, "Listener desconectado")
    }

    private fun extractNotificationText(notification: Notification?): String? {
        if (notification == null) return null

        val bundle = notification.extras
        val title = bundle.getString(Notification.EXTRA_TITLE) ?: ""
        val text = bundle.getString(Notification.EXTRA_TEXT) ?: ""
        val bigText = bundle.getString(Notification.EXTRA_BIG_TEXT) ?: ""
        val subText = bundle.getString(Notification.EXTRA_SUB_TEXT) ?: ""

        val lines = bundle.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
        val linesText = lines?.joinToString(" ")?.toString() ?: ""

        return buildString {
            append(title).append(" ")
            append(text).append(" ")
            append(bigText).append(" ")
            append(subText).append(" ")
            append(linesText)
        }.trim()
    }

    private suspend fun processNotification(packageName: String, text: String) {
        val result = parserRegistry.parse(packageName, text)

        when (result) {
            is ParseResult.Success -> {
                val rawMovement = result.movement
                Log.i(TAG, "Parseado: ${rawMovement.type} ${rawMovement.amount} ${rawMovement.counterpartyRaw} (${rawMovement.bankEntity})")
                
                // Pasar al pipeline de enriquecimiento y guardado
                enrichmentPipeline?.process(rawMovement)
            }
            is ParseResult.Failure -> {
                Log.w(TAG, "Error parseando notificación de $packageName: ${result.error}")
                Log.w(TAG, "Texto original: ${result.rawText}")
            }
        }
    }
}