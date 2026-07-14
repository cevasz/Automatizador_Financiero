package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.RawMovement
import java.time.Instant
import java.util.regex.Pattern

class NuParser : BankParser {
    override val bankEntity = BankEntity.NU
    override val supportedPackageNames = listOf("co.nubank", "br.com.nubank")

    override fun parse(notificationText: String): ParseResult {
        val lowerText = notificationText.lowercase()
        val isIncome = lowerText.contains("recibiste") || lowerText.contains("pix recibido") || lowerText.contains("transferencia recibida")
        val isExpense = lowerText.contains("pagaste") || lowerText.contains("pix enviado") || lowerText.contains("compra") || lowerText.contains("transferencia enviada")

        val amount = extractAmount(lowerText)
            ?: return ParseResult.Failure("No se pudo extraer monto", notificationText)

        val counterparty = extractCounterparty(lowerText)
        val paymentMethod = if (lowerText.contains("pix")) PaymentMethod.PSE else PaymentMethod.NU

        return ParseResult.Success(RawMovement(
            type = if (isIncome) MovementType.INCOME else MovementType.EXPENSE,
            amount = amount,
            paymentMethod = paymentMethod,
            counterpartyRaw = counterparty,
            date = Instant.now(),
            bankEntity = BankEntity.NU,
            rawText = notificationText,
            confidence = 0.8
        ))
    }

    private fun extractAmount(text: String): Long? {
        // Nu Colombia: "COP 50.000" o "$ 50.000"
        val patterns = listOf(
            Pattern.compile("COP\\s*([\\d.,]+)"),
            Pattern.compile("\\$\\s*([\\d.,]+)"),
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
        val patterns = listOf(
            Pattern.compile("de\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("para\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("en\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
        )
        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) return matcher.group(1).trim()
        }
        return "Nu Colombia"
    }
}