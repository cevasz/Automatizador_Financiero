package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.RawMovement
import java.time.Instant
import java.util.Locale
import java.util.regex.Pattern

class NequiParser : BaseBankParser(
    bankEntity = BankEntity.NEQUI,
    supportedPackageNames = listOf("com.nequi.app")
) {

    override fun parse(notificationText: String): ParseResult {
        val lowerText = notificationText.lowercase(Locale.getDefault())
        val type = when {
            lowerText.contains("recibiste") || lowerText.contains("transfirieron") || lowerText.contains("recibido") -> MovementType.INCOME
            lowerText.contains("enviaste") || lowerText.contains("pago") || lowerText.contains("pagaste") -> MovementType.EXPENSE
            else -> determineType(lowerText)
        }

        val amount = parseAmount(notificationText)
            ?: return ParseResult.Failure("No se pudo extraer monto", notificationText)

        val counterparty = extractCounterparty(lowerText)
        val paymentMethod = if (lowerText.contains("qr")) PaymentMethod.QR else PaymentMethod.NEQUI
        val date = parseDate(notificationText) ?: Instant.now()

        return buildRawMovement(
            type = type,
            amount = amount,
            paymentMethod = paymentMethod,
            counterpartyRaw = counterparty,
            date = date,
            rawText = notificationText,
            confidence = 0.9
        )
    }

    private fun extractCounterparty(text: String): String {
        val phonePattern = Pattern.compile("\\b(\\d{10})\\b")
        val matcher = phonePattern.matcher(text)
        if (matcher.find()) {
            return matcher.group(1)
        }
        val commercePattern = Pattern.compile("(?:en|de)\\s+([^\\.\\n]+)")
        val m2 = commercePattern.matcher(text)
        if (m2.find()) return m2.group(1).trim()
        return "Nequi"
    }
}