package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.RawMovement
import java.time.Instant
import java.util.regex.Pattern

class BancolombiaParser : BankParser {
    override val bankEntity = BankEntity.BANCOLOMBIA
    override val supportedPackageNames = listOf("com.bancolombia.certipersonas", "com.bancolombia.personas")

    override fun parse(notificationText: String): ParseResult {
        val lowerText = notificationText.lowercase()
        val isIncome = lowerText.contains("abono") || lowerText.contains("recibido") || lowerText.contains("transferencia recibida")
        val isExpense = lowerText.contains("retiro") || lowerText.contains("compra") || lowerText.contains("pago") || lowerText.contains("transferencia enviada")

        val amount = extractAmount(lowerText)
            ?: return ParseResult.Failure("No se pudo extraer monto", notificationText)

        val counterparty = extractCounterparty(lowerText)
        val paymentMethod = determinePaymentMethod(lowerText)

        return ParseResult.Success(RawMovement(
            type = if (isIncome) MovementType.INCOME else MovementType.EXPENSE,
            amount = amount,
            paymentMethod = paymentMethod,
            counterpartyRaw = counterparty,
            date = Instant.now(),
            bankEntity = BankEntity.BANCOLOMBIA,
            rawText = notificationText,
            confidence = 0.85
        ))
    }

    private fun extractAmount(text: String): Long? {
        // Bancolombia: "$ 50.000" o "COP 50.000"
        val patterns = listOf(
            Pattern.compile("\\$\\s*([\\d.,]+)"),
            Pattern.compile("COP\\s*([\\d.,]+)"),
            Pattern.compile("([\\d]{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?)"),
        )
        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val amountStr = matcher.group(1).replace(",", "").replace(".", "")
                return try { amountStr.toLong() } catch (e: NumberFormatException) { null }
            }
        }
        return null
    }

    private fun extractCounterparty(text: String): String {
        // Buscar "a nombre de X" o "de X" o "en X"
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