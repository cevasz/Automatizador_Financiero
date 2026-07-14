package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.RawMovement
import java.time.Instant
import java.util.regex.Pattern

class NequiParser : BankParser {
    override val bankEntity = BankEntity.NEQUI
    override val supportedPackageNames = listOf("com.nequi.app")

    private val patterns = listOf(
        // "Recibiste $50.000 de 320XXXXXXX"
        Pattern.compile("recibiste\\s+\\$?([\\d.,]+)\\s+de\\s+(\\d{10})", Pattern.CASE_INSENSITIVE),
        // "Enviaste $50.000 a 320XXXXXXX"
        Pattern.compile("enviaste\\s+\\$?([\\d.,]+)\\s+a\\s+(\\d{10})", Pattern.CASE_INSENSITIVE),
        // "Te transfirieron $50.000"
        Pattern.compile("te\\s+transfirieron\\s+\\$?([\\d.,]+)", Pattern.CASE_INSENSITIVE),
        // "Pago QR $50.000 en Comercio"
        Pattern.compile("pago\\s+qr\\s+\\$?([\\d.,]+)\\s+en\\s+(.+)", Pattern.CASE_INSENSITIVE),
    )

    override fun parse(notificationText: String): ParseResult {
        val lowerText = notificationText.lowercase()
        val isIncome = lowerText.contains("recibiste") || lowerText.contains("transfirieron") || lowerText.contains("recibido")
        val isExpense = lowerText.contains("enviaste") || lowerText.contains("pago") || lowerText.contains("pagaste")

        val amount = extractAmount(lowerText)
            ?: return ParseResult.Failure("No se pudo extraer monto", notificationText)

        val counterparty = extractCounterparty(lowerText)
        val paymentMethod = if (lowerText.contains("qr")) PaymentMethod.QR else PaymentMethod.NEQUI

        return ParseResult.Success(RawMovement(
            type = if (isIncome) MovementType.INCOME else MovementType.EXPENSE,
            amount = amount,
            paymentMethod = paymentMethod,
            counterpartyRaw = counterparty,
            date = Instant.now(),
            bankEntity = BankEntity.NEQUI,
            rawText = notificationText,
            confidence = 0.9
        ))
    }

    private fun extractAmount(text: String): Long? {
        val patterns = listOf(
            Pattern.compile("\\$?([\\d]{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?)"),
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
        // Buscar número de 10 dígitos (Nequi usa números de celular)
        val phonePattern = Pattern.compile("\\b(\\d{10})\\b")
        val matcher = phonePattern.matcher(text)
        if (matcher.find()) {
            return matcher.group(1)
        }
        // Buscar "en Comercio" o "de Nombre"
        val commercePattern = Pattern.compile("(?:en|de)\\s+([^\\.\\n]+)")
        val m2 = commercePattern.matcher(text)
        if (m2.find()) return m2.group(1).trim()
        return "Nequi"
    }
}