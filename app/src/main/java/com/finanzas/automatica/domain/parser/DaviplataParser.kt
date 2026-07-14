package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.RawMovement
import java.time.Instant
import java.util.regex.Pattern

class DaviplataParser : BankParser {
    override val bankEntity = BankEntity.DAVIPLATA
    override val supportedPackageNames = listOf("com.daviplata.daviplata")

    override fun parse(notificationText: String): ParseResult {
        val lowerText = notificationText.lowercase()
        val isIncome = lowerText.contains("recibiste") || lowerText.contains("abono") || lowerText.contains("te enviaron")
        val isExpense = lowerText.contains("enviaste") || lowerText.contains("pagaste") || lowerText.contains("retiro") || lowerText.contains("compra")

        val amount = extractAmount(lowerText)
            ?: return ParseResult.Failure("No se pudo extraer monto", notificationText)

        val counterparty = extractCounterparty(lowerText)
        val paymentMethod = if (lowerText.contains("qr")) PaymentMethod.QR else PaymentMethod.DAVIPLATA

        return ParseResult.Success(RawMovement(
            type = if (isIncome) MovementType.INCOME else MovementType.EXPENSE,
            amount = amount,
            paymentMethod = paymentMethod,
            counterpartyRaw = counterparty,
            date = Instant.now(),
            bankEntity = BankEntity.DAVIPLATA,
            rawText = notificationText,
            confidence = 0.85
        ))
    }

    private fun extractAmount(text: String): Long? {
        val patterns = listOf(
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