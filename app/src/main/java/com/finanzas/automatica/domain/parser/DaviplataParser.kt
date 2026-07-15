package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.RawMovement
import java.time.Instant
import java.util.Locale
import java.util.regex.Pattern

class DaviplataParser : BaseBankParser(
    bankEntity = BankEntity.DAVIPLATA,
    supportedPackageNames = listOf("com.daviplata.daviplata")
) {

    override fun parse(notificationText: String): ParseResult {
        val lowerText = notificationText.lowercase(Locale.getDefault())
        val type = when {
            lowerText.contains("recibiste") || lowerText.contains("abono") || lowerText.contains("te enviaron") -> MovementType.INCOME
            lowerText.contains("enviaste") || lowerText.contains("pagaste") || lowerText.contains("retiro") || lowerText.contains("compra") -> MovementType.EXPENSE
            else -> determineType(lowerText)
        }

        val amount = parseAmount(notificationText)
            ?: return ParseResult.Failure("No se pudo extraer monto", notificationText)

        val counterparty = extractCounterparty(lowerText)
        val paymentMethod = if (lowerText.contains("qr")) PaymentMethod.QR else PaymentMethod.DAVIPLATA
        val date = parseDate(notificationText) ?: Instant.now()

        return buildRawMovement(
            type = type,
            amount = amount,
            paymentMethod = paymentMethod,
            counterpartyRaw = counterparty,
            date = date,
            rawText = notificationText,
            confidence = 0.85
        )
    }

    private fun extractCounterparty(text: String): String {
        val patterns = listOf(
            Pattern.compile("de\\s+(\\d{10})", Pattern.CASE_INSENSITIVE),
            Pattern.compile("a\\s+(\\d{10})", Pattern.CASE_INSENSITIVE),
            Pattern.compile("en\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
        )
        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) return matcher.group(1).trim()
        }
        return "Daviplata"
    }
}