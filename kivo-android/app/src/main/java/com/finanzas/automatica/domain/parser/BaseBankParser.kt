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

abstract class BaseBankParser(
    override val bankEntity: BankEntity,
    override val supportedPackageNames: List<String>
) : BankParser {

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

    private val financialKeywords = listOf(
        "pago", "compra", "transferencia", "giro", "retiro", "debito", "débito", "pagaste", "enviaste", "transfirieron",
        "recibiste", "abono", "ingreso", "recarga", "devolución", "devolucion", "crédito", "credito", "saldo", "pse", "qr", "aprobado", "rechazado", "exitosa"
    )

    // Notificaciones no transaccionales (promocionales, de seguridad, marketing) que
    // suelen venir de la app oficial del banco y por eso pasan el filtro de paquete/nombre,
    // pero no representan un movimiento real y no deben insertarse como ingreso/egreso.
    private val excludedKeywords = listOf(
        "descuento", "promoción", "promocion", "cupón", "cupon", "oferta",
        "código de verificación", "codigo de verificacion", "clave dinámica", "clave dinamica",
        "otp", "puntos", "cuotas sin interés", "cuotas sin interes",
        "actualiza tu app", "actualiza la app", "nueva versión disponible", "nueva version disponible",
        "términos y condiciones", "terminos y condiciones", "encuesta", "califica tu experiencia",
        "actívalo", "activalo", "descubre", "conoce"
    )

    override fun canParse(packageName: String, notificationText: String): Boolean {
        val matchesPackage = supportedPackageNames.any { packageName.contains(it, ignoreCase = true) }
        val lower = notificationText.lowercase(Locale.getDefault())

        val bankKeywordMatches = when (bankEntity) {
            BankEntity.BANCOLOMBIA -> lower.contains("bancolombia")
            BankEntity.NEQUI -> lower.contains("nequi")
            BankEntity.DAVIPLATA -> lower.contains("daviplata") || lower.contains("davivienda")
            BankEntity.NU -> lower.contains("nu ") || lower.contains("nubank")
            BankEntity.LULO -> lower.contains("lulo")
            else -> false
        }

        val isSmsApp = messagingPackages.any { packageName.contains(it, ignoreCase = true) }

        // Si es app de mensajería (SMS/Gmail), debe mencionar explícitamente el banco
        if (isSmsApp && !bankKeywordMatches) {
            return false
        }

        // Si no coincide por nombre de paquete oficial ni por palabra clave del banco, descartar
        if (!matchesPackage && !bankKeywordMatches) {
            return false
        }

        // Descartar notificaciones promocionales/de seguridad aunque vengan de la app oficial:
        // no son movimientos reales y no deben generar ingresos/egresos falsos.
        if (excludedKeywords.any { lower.contains(it) }) {
            return false
        }

        // REGLA CLAVE: Verificar que la notificación sea sobre un movimiento financiero.
        // Debe contener un monto de dinero con marca de moneda explícita ($ o COP) — un
        // número suelto (hora, porcentaje, número de cuotas, código) NO cuenta como monto,
        // para no confundir notificaciones no transaccionales con movimientos reales.
        val hasAmount = hasCurrencyMarkedAmount(notificationText)
        val hasFinancialKeyword = financialKeywords.any { lower.contains(it) }

        return hasAmount || hasFinancialKeyword
    }

    /**
     * true si el texto contiene un monto con marca de moneda explícita ($ o COP).
     * A diferencia de [parseAmount], deliberadamente NO acepta números sueltos sin
     * contexto de moneda — se usa solo como señal en [canParse] para decidir si una
     * notificación es realmente financiera.
     */
    private fun hasCurrencyMarkedAmount(text: String): Boolean {
        return CURRENCY_MARKED_PATTERNS.any { it.matcher(text).find() }
    }

    protected fun parseAmount(text: String): Long? {
        // Patrones con marca de moneda explícita: $ 50.000, $50,000, 50.000 COP
        for (pattern in CURRENCY_MARKED_PATTERNS) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                ColombianAmountParser.toCents(matcher.group(1))?.let { return it }
            }
        }

        // Último recurso: número sin marca de moneda, pero solo si tiene forma real de
        // monto (agrupado en miles, ej. "50.000", o al menos 4 dígitos seguidos, ej.
        // "50000"). Evita capturar horas ("10:30"), porcentajes ("20%"), número de
        // cuotas ("3 cuotas"), etc. como si fueran el monto de la transacción.
        val matcher = BARE_AMOUNT_PATTERN.matcher(text)
        if (matcher.find()) {
            ColombianAmountParser.toCents(matcher.group(1))?.let { return it }
        }
        return null
    }

    protected fun parseDate(text: String): Instant? {
        // Patrones de fecha comunes en notificaciones colombianas
        val patterns = listOf(
            DatePattern(Pattern.compile("\\bhoy\\b", Pattern.CASE_INSENSITIVE), DatePatternType.TODAY),
            DatePattern(Pattern.compile("\\bayer\\b", Pattern.CASE_INSENSITIVE), DatePatternType.YESTERDAY),
            DatePattern(Pattern.compile("(\\d{1,2})[/-](\\d{1,2})[/-](\\d{2,4})"), DatePatternType.DMY),
            DatePattern(Pattern.compile("(\\d{1,2})\\s+de\\s+(\\w+)\\s+de\\s+(\\d{4})", Pattern.CASE_INSENSITIVE), DatePatternType.DAY_MONTH_YEAR)
        )

        for (pattern in patterns) {
            val matcher = pattern.pattern.matcher(text)
            if (matcher.find()) {
                return when (pattern.type) {
                    DatePatternType.TODAY -> Instant.now()
                    DatePatternType.YESTERDAY -> Instant.now().minusSeconds(86400)
                    DatePatternType.DMY -> {
                        try {
                            val day = matcher.group(1).toInt()
                            val month = matcher.group(2).toInt()
                            val year = matcher.group(3).toInt()
                            val fullYear = if (year < 100) year + 2000 else year
                            LocalDateTime.of(fullYear, month, day, 12, 0)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                        } catch (e: Exception) {
                            null
                        }
                    }
                    DatePatternType.DAY_MONTH_YEAR -> {
                        try {
                            val day = matcher.group(1).toInt()
                            val month = parseMonthName(matcher.group(2))
                            val year = matcher.group(3).toInt()
                            LocalDateTime.of(year, month, day, 12, 0)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
            }
        }
        return Instant.now() // Default a ahora si no se puede parsear
    }

    private enum class DatePatternType {
        TODAY, YESTERDAY, DMY, DAY_MONTH_YEAR
    }

    private data class DatePattern(
        val pattern: Pattern,
        val type: DatePatternType
    )

    private fun parseMonthName(name: String): Int {
        return when (name.lowercase(Locale.getDefault()).take(3)) {
            "ene" -> 1
            "feb" -> 2
            "mar" -> 3
            "abr" -> 4
            "may" -> 5
            "jun" -> 6
            "jul" -> 7
            "ago" -> 8
            "sep" -> 9
            "oct" -> 10
            "nov" -> 11
            "dic" -> 12
            else -> 1
        }
    }

    protected fun determineType(text: String): MovementType {
        val lower = text.lowercase(Locale.getDefault())
        val expenseKeywords = listOf("pago", "compra", "transferencia enviada", "giro", "retiro", "debito", "pagaste")
        val incomeKeywords = listOf("recibiste", "abono", "transferencia recibida", "ingreso", "recarga", "devolución", "crédito")

        return when {
            expenseKeywords.any { lower.contains(it) } -> MovementType.EXPENSE
            incomeKeywords.any { lower.contains(it) } -> MovementType.INCOME
            else -> MovementType.EXPENSE // Default conservador
        }
    }

    protected fun buildRawMovement(
        type: MovementType,
        amount: Long,
        paymentMethod: PaymentMethod,
        counterpartyRaw: String,
        date: Instant,
        rawText: String,
        confidence: Double = 0.8
    ): ParseResult.Success {
        return ParseResult.Success(RawMovement(
            type = type,
            amount = amount,
            paymentMethod = paymentMethod,
            counterpartyRaw = counterpartyRaw,
            date = date,
            bankEntity = bankEntity,
            rawText = rawText,
            confidence = confidence
        ))
    }

    companion object {
        private val CURRENCY_MARKED_PATTERNS = listOf(
            Pattern.compile("\\$\\s*([\\d.,]+)"),
            Pattern.compile("([\\d.,]+)\\s*COP")
        )
        private val BARE_AMOUNT_PATTERN =
            Pattern.compile("\\b(\\d{1,3}(?:[.,]\\d{3})+(?:[.,]\\d{1,2})?|\\d{4,})\\b")
    }
}