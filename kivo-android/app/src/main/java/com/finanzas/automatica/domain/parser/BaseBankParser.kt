package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.RawMovement
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale
import java.util.regex.Pattern

/**
 * Motor común de lectura de notificaciones bancarias.
 *
 * Cada banco solo aporta lo que es realmente suyo: cómo se llama, con qué paquetes y
 * palabras se reconoce, sus redacciones propias ([extraSignals]) y cómo se lee la
 * contraparte. La decisión de si la notificación es una transacción, cuál es el monto y
 * si entra o sale plata la toma este motor con el [TransactionLexicon] compartido, para
 * que la misma transacción no se clasifique distinto según el banco que la reportó.
 *
 * @param bankNamePattern reconoce el banco cuando la notificación NO viene de la app
 *   oficial (SMS o correo reenviado por el banco).
 * @param displayName contraparte de último recurso cuando el texto no nombra a nadie.
 */
abstract class BaseBankParser(
    override val bankEntity: BankEntity,
    override val supportedPackageNames: List<String>,
    final override val bankNamePattern: Regex,
    private val defaultPaymentMethod: PaymentMethod,
    private val displayName: String,
    private val baseConfidence: Double = 0.85
) : BankParser {

    /** Redacciones propias del banco. Se resuelven por peso junto con las compartidas. */
    protected open val extraSignals: List<TransactionLexicon.Signal> = emptyList()

    private val messagingPackages = listOf(
        "com.google.android.apps.messaging",
        "com.samsung.android.messaging",
        "com.android.mms",
        "com.android.messaging",
        "com.google.android.gm",
        "com.samsung.android.email",
        "mail",
        "messaging",
        "mms",
        "sms"
    )

    override fun canParse(packageName: String, notificationText: String): Boolean =
        evaluate(packageName, notificationText) is CaptureDecision.Accepted

    /**
     * Misma decisión que [canParse], pero diciendo en qué paso se cayó la notificación.
     * El orden de los pasos importa: los descartes de conversación personal van primero
     * para que su texto nunca llegue al registro de diagnóstico (ver [CaptureRejection]).
     */
    override fun evaluate(packageName: String, notificationText: String): CaptureDecision {
        val normalized = NotificationText.normalize(notificationText)
        val matchesPackage = supportedPackageNames.any { packageName.contains(it, ignoreCase = true) }
        val bankMention = bankNamePattern.find(normalized)
        val isMessagingApp = messagingPackages.any { packageName.contains(it, ignoreCase = true) }

        // En SMS y correo conviven los avisos del banco con conversaciones normales, así
        // que ahí se pide que el banco sea el REMITENTE: su nombre abre el texto, porque
        // el título de la notificación es quien la envía y los avisos reales empiezan por
        // "Bancolombia: ...". Un conocido que escriba "ya te mandé la plata por Nequi"
        // nombra al banco en mitad de la frase y no pasa de aquí.
        if (isMessagingApp) {
            if (bankMention == null) return rejected(CaptureRejection.MESSAGING_WITHOUT_BANK)
            if (bankMention.range.first > MESSAGING_SENDER_WINDOW) {
                return rejected(CaptureRejection.MESSAGING_SENDER_NOT_BANK)
            }
        }

        if (!matchesPackage && bankMention == null) return rejected(CaptureRejection.OTHER_APP)

        // Un movimiento SIEMPRE trae un monto con marca de moneda. Exigirlo de entrada
        // descarta de una vez códigos de seguridad, avisos de puntos y marketing con
        // números sueltos, sin depender de listas de palabras prohibidas.
        if (!AmountExtractor.hasCurrencyAmount(notificationText)) {
            return rejected(CaptureRejection.NO_AMOUNT)
        }

        if (TransactionLexicon.isHardBlocked(normalized)) {
            return rejected(CaptureRejection.NOT_A_MOVEMENT)
        }

        val detection = TransactionLexicon.detect(normalized, extraSignals)

        // Desde mensajería se exige además un verbo transaccional inequívoco.
        if (isMessagingApp) {
            return if (detection != null && detection.isStrong) {
                CaptureDecision.Accepted
            } else {
                rejected(CaptureRejection.MESSAGING_WEAK_VERB)
            }
        }

        // Un verbo transaccional fuerte manda sobre el ruido promocional: las compras
        // reales vienen con "a 3 cuotas sin interés", "ganaste puntos" o "si no reconoces
        // esta compra" en el mismo texto, y antes eso borraba el egreso.
        if (detection != null && detection.isStrong) return CaptureDecision.Accepted

        if (TransactionLexicon.isSoftBlocked(normalized) || TransactionLexicon.isPromotional(normalized)) {
            return rejected(CaptureRejection.REMINDER_OR_PROMO)
        }

        return if (detection != null || TransactionLexicon.hasGenericFinancialContext(normalized)) {
            CaptureDecision.Accepted
        } else {
            rejected(CaptureRejection.NO_TRANSACTION_WORDS)
        }
    }

    private fun rejected(reason: CaptureRejection): CaptureDecision =
        CaptureDecision.Rejected(reason)

    override fun parse(notificationText: String, occurredAt: Instant?): ParseResult {
        val normalized = NotificationText.normalize(notificationText)

        if (TransactionLexicon.isHardBlocked(normalized)) {
            return ParseResult.Failure(CaptureRejection.NOT_A_MOVEMENT.label, notificationText)
        }

        val amount = AmountExtractor.extractCents(notificationText)
            ?: return ParseResult.Failure(CaptureRejection.NO_AMOUNT_ON_PARSE.label, notificationText)

        val detection = TransactionLexicon.detect(normalized, extraSignals)
        val type = detection?.type ?: MovementType.EXPENSE

        // Sin verbo reconocido el tipo es una suposición (default conservador: egreso).
        // Bajar la confianza deja el movimiento pendiente de confirmación en vez de
        // ensuciar el historial en silencio, que era la queja de fondo: egresos que
        // aparecían donde había un ingreso.
        val confidence = when {
            detection == null -> 0.45
            detection.isStrong -> baseConfidence
            else -> baseConfidence - 0.20
        }

        return ParseResult.Success(
            RawMovement(
                type = type,
                amount = amount,
                paymentMethod = paymentMethodFor(normalized),
                counterpartyRaw = extractCounterparty(notificationText, normalized, type),
                date = parseDate(notificationText) ?: occurredAt ?: Instant.now(),
                bankEntity = bankEntity,
                rawText = notificationText,
                confidence = confidence
            )
        )
    }

    /**
     * Medio de pago. El default cubre lo transversal (QR, PSE, efectivo en cajero) y cae
     * al medio propio del banco; un parser puede refinarlo con sus productos.
     */
    protected open fun paymentMethodFor(normalized: String): PaymentMethod = when {
        Regex("\\bqr\\b").containsMatchIn(normalized) -> PaymentMethod.QR
        Regex("\\bpse\\b").containsMatchIn(normalized) -> PaymentMethod.PSE
        Regex("\\b(cajero|efectivo|corresponsal)\\b").containsMatchIn(normalized) -> PaymentMethod.CASH
        else -> defaultPaymentMethod
    }

    /**
     * Quién está al otro lado. Se lee del texto ORIGINAL para conservar el nombre tal
     * como lo escribió el banco ("LUIS RINCON"), y la preposición se elige según el
     * sentido del movimiento: de quién llegó (ingreso) o a quién/dónde se pagó (egreso).
     */
    protected open fun extractCounterparty(
        originalText: String,
        normalized: String,
        type: MovementType
    ): String {
        phoneNumber(originalText)?.let { return it }

        val prepositions = if (type == MovementType.INCOME) {
            listOf("de", "desde", "por parte de", "en")
        } else {
            listOf("en", "a", "para", "hacia", "de")
        }

        for (preposition in prepositions) {
            candidatesAfter(originalText, preposition)
                .firstOrNull { isUsableCounterparty(it) }
                ?.let { return cleanCounterparty(it) }
        }
        return displayName
    }

    private fun phoneNumber(text: String): String? {
        val matcher = PHONE_PATTERN.matcher(text)
        return if (matcher.find()) matcher.group(1) else null
    }

    private fun candidatesAfter(text: String, preposition: String): List<String> {
        val pattern = Pattern.compile(
            "\\b" + Pattern.quote(preposition) + "\\s+([^,.;\\n]+)",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = pattern.matcher(text)
        val result = mutableListOf<String>()
        while (matcher.find()) {
            matcher.group(1)?.let { result += it }
        }
        return result
    }

    /** Descarta capturas que son montos, fechas, cuentas o relleno gramatical. */
    private fun isUsableCounterparty(candidate: String): Boolean {
        val cleaned = cleanCounterparty(candidate)
        if (cleaned.length < 3 || cleaned.length > 60) return false
        if (cleaned.contains('$')) return false
        if (bankNamePattern.containsMatchIn(NotificationText.normalize(cleaned))) return false

        val digitsOnly = cleaned.all { it.isDigit() }
        // Un número suelto solo sirve si es un celular (destino típico en billeteras);
        // "de $ 300.000 COP" o "de 2026" no son contrapartes.
        if (digitsOnly) return cleaned.length in 7..11

        return !FILLER_PATTERN.containsMatchIn(NotificationText.normalize(cleaned))
    }

    private fun cleanCounterparty(candidate: String): String {
        var value = candidate.trim()
        // Corta la cola de la frase: "LUIS RINCON en tu cuenta **3463 el 11/08/2026".
        value = TAIL_PATTERN.split(value).first().trim()
        return value.trim(' ', '-', ':', '*', '"', '\'', '(', ')')
    }

    /** Fecha escrita en el texto, o `null` si la notificacion no la trae. */
    protected fun parseDate(text: String): Instant? {
        for (pattern in DATE_PATTERNS) {
            val matcher = pattern.pattern.matcher(text)
            if (!matcher.find()) continue
            val parsed = when (pattern.type) {
                DatePatternType.TODAY -> Instant.now()
                DatePatternType.YESTERDAY -> Instant.now().minusSeconds(86_400)
                DatePatternType.DMY -> runCatching {
                    val day = matcher.group(1)!!.toInt()
                    val month = matcher.group(2)!!.toInt()
                    val year = matcher.group(3)!!.toInt().let { if (it < 100) it + 2000 else it }
                    atTime(year, month, day, text)
                }.getOrNull()
                DatePatternType.DAY_MONTH_YEAR -> runCatching {
                    val day = matcher.group(1)!!.toInt()
                    val month = parseMonthName(matcher.group(2)!!)
                    // Muchas notificaciones omiten el ano ("el 3 de marzo"): se asume el
                    // actual, que es lo unico razonable para un movimiento recien hecho.
                    val year = matcher.group(3)?.toInt() ?: LocalDateTime.now().year
                    atTime(year, month, day, text)
                }.getOrNull()
            }
            if (parsed != null) return parsed
        }
        return null
    }

    /**
     * Usa la hora del texto cuando el banco la incluye ("el 11/08/2026 a las 18:43"). La
     * hora importa: [EnrichmentPipeline] deduplica por ventana de minutos, y clavar todo
     * a las 12:00 hacía que el mismo movimiento reportado por dos canales no se
     * reconociera como duplicado.
     */
    private fun atTime(year: Int, month: Int, day: Int, text: String): Instant {
        val matcher = TIME_PATTERN.matcher(text)
        var hour = 12
        var minute = 0
        if (matcher.find()) {
            hour = matcher.group(1)!!.toInt()
            minute = matcher.group(2)!!.toInt()
            val meridiem = matcher.group(3)?.lowercase(Locale.ROOT)?.replace(".", "")
            if (meridiem == "pm" && hour < 12) hour += 12
            if (meridiem == "am" && hour == 12) hour = 0
            if (hour !in 0..23 || minute !in 0..59) {
                hour = 12
                minute = 0
            }
        }
        return LocalDateTime.of(year, month, day, hour, minute)
            .atZone(ZoneId.systemDefault())
            .toInstant()
    }

    private fun parseMonthName(name: String): Int = when (name.lowercase(Locale.ROOT).take(3)) {
        "ene" -> 1
        "feb" -> 2
        "mar" -> 3
        "abr" -> 4
        "may" -> 5
        "jun" -> 6
        "jul" -> 7
        "ago" -> 8
        "sep", "set" -> 9
        "oct" -> 10
        "nov" -> 11
        "dic" -> 12
        else -> 1
    }

    private enum class DatePatternType { TODAY, YESTERDAY, DMY, DAY_MONTH_YEAR }

    private data class DatePattern(val pattern: Pattern, val type: DatePatternType)

    companion object {
        /**
         * Margen inicial en el que debe aparecer el nombre del banco para aceptar un SMS
         * o correo: cubre el remitente ("Bancolombia:") y los encabezados cortos por
         * codigo numerico ("87400 Bancolombia le informa...").
         */
        private const val MESSAGING_SENDER_WINDOW = 25

        private val PHONE_PATTERN = Pattern.compile("\\b(3\\d{9})\\b")

        private val TIME_PATTERN = Pattern.compile(
            "\\b(\\d{1,2}):(\\d{2})(?:\\s*(a\\.?m\\.?|p\\.?m\\.?))?",
            Pattern.CASE_INSENSITIVE
        )

        // Palabras que indican que lo capturado es relleno y no un nombre de comercio o
        // persona ("de una transferencia", "en tu cuenta", "a las 18:43").
        private val FILLER_PATTERN = Regex(
            "^(tu|su|tus|sus|la|el|los|las|un|una|unos|unas|mi|mis)\\b|" +
                "^(cuenta|tarjeta|transferencia|transaccion|dinero|plata|pesos|saldo|" +
                "bolsillo|billetera|nequi|ahorro|ahorros|credito|nomina|cop|las|forma)\\b"
        )

        // Corta la cola de la frase donde empieza otro dato de la notificación.
        private val TAIL_PATTERN = Regex(
            "\\s+(?:en tu|en su|en la|desde tu|desde su|el \\d|a las|por \\$|con tu|con su|" +
                "cuenta|ref\\.?|referencia)\\b",
            RegexOption.IGNORE_CASE
        )

        private val DATE_PATTERNS = listOf(
            DatePattern(Pattern.compile("(\\d{1,2})[/-](\\d{1,2})[/-](\\d{2,4})"), DatePatternType.DMY),
            DatePattern(
                Pattern.compile("\\b(\\d{1,2})\\s+de\\s+(\\p{L}+)(?:\\s+de\\s+(\\d{4}))?", Pattern.CASE_INSENSITIVE),
                DatePatternType.DAY_MONTH_YEAR
            ),
            DatePattern(Pattern.compile("\\bhoy\\b", Pattern.CASE_INSENSITIVE), DatePatternType.TODAY),
            DatePattern(Pattern.compile("\\bayer\\b", Pattern.CASE_INSENSITIVE), DatePatternType.YESTERDAY)
        )
    }
}
