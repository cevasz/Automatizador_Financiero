package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.RawMovement
import java.time.Instant
import java.util.Locale
import java.util.regex.Pattern

class LuloParser : BaseBankParser(
    bankEntity = BankEntity.LULO,
    supportedPackageNames = listOf("com.lulobank.app", "co.lulobank")
) {

    override fun parse(notificationText: String): ParseResult {
        val lowerText = notificationText.lowercase(Locale.getDefault())

        val type = when {
            lowerText.contains("abono") || lowerText.contains("recibiste") || lowerText.contains("te depositaron") -> MovementType.INCOME
            lowerText.contains("compra") || lowerText.contains("pago") || lowerText.contains("retiro") || lowerText.contains("enviaste") -> MovementType.EXPENSE
            else -> determineType(lowerText)
        }

        val amount = parseAmount(notificationText) ?: return ParseResult.Failure("No se pudo extraer monto", notificationText)
        val counterparty = extractCounterparty(notificationText)
        val paymentMethod = if (lowerText.contains("qr")) PaymentMethod.QR else PaymentMethod.LULO
        val date = parseDate(notificationText) ?: Instant.now()

        return buildRawMovement(
            type = type,
            amount = amount,
            paymentMethod = paymentMethod,
            counterpartyRaw = counterparty,
            date = date,
            rawText = notificationText,
            confidence = 0.8
        )
    }

    private fun extractCounterparty(text: String): String {
        val patterns = listOf(
            Pattern.compile("en\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("de\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("a\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
        )
        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) return matcher.group(1).trim()
        }
        return "Lulo Bank"
    }
}