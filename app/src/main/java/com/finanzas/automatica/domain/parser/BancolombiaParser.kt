package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.RawMovement
import java.time.Instant
import java.util.Locale
import java.util.regex.Pattern

class BancolombiaParser : BaseBankParser(
    bankEntity = BankEntity.BANCOLOMBIA,
    supportedPackageNames = listOf("com.bancolombia.certipersonas", "com.bancolombia.personas")
) {

    override fun parse(notificationText: String): ParseResult {
        val lowerText = notificationText.lowercase(Locale.getDefault())
        val type = when {
            lowerText.contains("abono") || lowerText.contains("recibido") || lowerText.contains("transferencia recibida") -> MovementType.INCOME
            lowerText.contains("retiro") || lowerText.contains("compra") || lowerText.contains("pago") || lowerText.contains("transferencia enviada") -> MovementType.EXPENSE
            else -> determineType(lowerText)
        }

        val amount = parseAmount(notificationText)
            ?: return ParseResult.Failure("No se pudo extraer monto", notificationText)

        val counterparty = extractCounterparty(lowerText)
        val paymentMethod = determinePaymentMethod(lowerText)
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
            Pattern.compile("a\\s+nombre\\s+de\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bde\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\ben\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
        )
        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) return matcher.group(1).trim()
        }
        return "Bancolombia"
    }

    private fun determinePaymentMethod(text: String): PaymentMethod {
        return when {
            text.contains("qr") -> PaymentMethod.QR
            text.contains("pse") -> PaymentMethod.PSE
            text.contains("nequi") -> PaymentMethod.NEQUI
            else -> PaymentMethod.BANCOLOMBIA
        }
    }
}