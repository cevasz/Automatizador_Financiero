package com.finanzas.automatica.domain.parser

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

    override fun canParse(packageName: String, notificationText: String): Boolean {
        return supportedPackageNames.any { packageName.contains(it, ignoreCase = true) }
    }

    protected fun parseAmount(text: String): Long? {
        // Busca patrones como: $ 50.000, $50.000, 50.000 COP, 50000
        val patterns = listOf(
            Pattern.compile("\\$\\s*([\\d.,]+)"),
            Pattern.compile("([\\d.,]+)\\s*COP"),
            Pattern.compile("([\\d]{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?)")
        )

        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val amountStr = matcher.group(1).replace(",", "").replace(".", "")
                return try {
                    amountStr.toLong()
                } catch (e: NumberFormatException) {
                    null
                }
            }
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
}