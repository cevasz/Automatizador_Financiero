package com.finanzas.automatica.domain.importer

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.RawMovement
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.regex.Pattern

data class ImportSummary(
    val totalCount: Int,
    val incomeCount: Int,
    val expenseCount: Int,
    val totalIncomeAmount: Long,
    val totalExpenseAmount: Long,
    val importedMovements: List<RawMovement>
)

object StatementImporter {

    private val DATE_PATTERNS = listOf(
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()),
        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()),
        DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault()),
        DateTimeFormatter.ofPattern("dd/MM/yy", Locale.getDefault())
    )

    /**
     * Parsea un texto plano, CSV o exportación de extracto bancario.
     */
    fun parseStatementText(
        text: String,
        defaultBank: BankEntity = BankEntity.BANCOLOMBIA
    ): ImportSummary {
        val lines = text.lines()
        val movements = mutableListOf<RawMovement>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isBlank() || trimmed.startsWith("#") || trimmed.startsWith("//")) continue

            val movement = parseLine(trimmed, defaultBank)
            if (movement != null) {
                movements.add(movement)
            }
        }

        val incomeList = movements.filter { it.type == MovementType.INCOME }
        val expenseList = movements.filter { it.type == MovementType.EXPENSE }

        return ImportSummary(
            totalCount = movements.size,
            incomeCount = incomeList.size,
            expenseCount = expenseList.size,
            totalIncomeAmount = incomeList.sumOf { it.amount },
            totalExpenseAmount = expenseList.sumOf { it.amount },
            importedMovements = movements
        )
    }

    private fun parseLine(line: String, defaultBank: BankEntity): RawMovement? {
        val tokens = if (line.contains(";")) line.split(";") else line.split(",")

        // Si es CSV estructurado de 3+ columnas (Fecha, Descripción, Monto)
        if (tokens.size >= 3) {
            val dateStr = tokens[0].trim().removeSurrounding("\"")
            val descStr = tokens[1].trim().removeSurrounding("\"")
            val amountStr = tokens[2].trim().removeSurrounding("\"")

            val date = parseDateStr(dateStr) ?: return null
            val amountParsed = parseAmountVal(amountStr) ?: return null

            val isExpense = amountStr.startsWith("-") || descStr.contains("compra", ignoreCase = true) || descStr.contains("pago", ignoreCase = true) || descStr.contains("retiro", ignoreCase = true)
            val type = if (isExpense) MovementType.EXPENSE else MovementType.INCOME

            return RawMovement(
                type = type,
                amount = Math.abs(amountParsed),
                paymentMethod = mapPaymentMethod(defaultBank),
                counterpartyRaw = descStr.ifBlank { defaultBank.name },
                date = date,
                bankEntity = defaultBank,
                rawText = line,
                confidence = 0.95
            )
        }

        // Si es texto de extracto libre (ej: 11/08/2026 Transferencia LUIS RINCON $100,000)
        val datePattern = Pattern.compile("(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})")
        val amountPattern = Pattern.compile("[-+]?\\$\\s*([\\d.,]+)|[-+]?([\\d.]{4,})")

        val dateMatcher = datePattern.matcher(line)
        val amountMatcher = amountPattern.matcher(line)

        if (dateMatcher.find() && amountMatcher.find()) {
            val dateStr = dateMatcher.group(1)
            val amountRaw = amountMatcher.group(0)

            val date = parseDateStr(dateStr) ?: Instant.now()
            val amountParsed = parseAmountVal(amountRaw) ?: return null

            val lower = line.lowercase(Locale.getDefault())
            val type = if (amountRaw.contains("-") || lower.contains("compra") || lower.contains("pago") || lower.contains("retiro") || lower.contains("debito")) {
                MovementType.EXPENSE
            } else {
                MovementType.INCOME
            }

            val desc = line
                .replace(dateStr, "")
                .replace(amountRaw, "")
                .replace(Regex("[$;,]"), "")
                .trim()
                .ifBlank { defaultBank.name }

            return RawMovement(
                type = type,
                amount = Math.abs(amountParsed),
                paymentMethod = mapPaymentMethod(defaultBank),
                counterpartyRaw = desc,
                date = date,
                bankEntity = defaultBank,
                rawText = line,
                confidence = 0.90
            )
        }

        return null
    }

    private fun parseDateStr(dateStr: String): Instant? {
        for (formatter in DATE_PATTERNS) {
            try {
                val localDate = LocalDate.parse(dateStr, formatter)
                return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            } catch (_: Exception) {
            }
        }
        return null
    }

    private fun parseAmountVal(amountStr: String): Long? {
        val clean = amountStr.replace("$", "").replace(" ", "").replace(".", "").replace(",", "")
        val rawNum = clean.toLongOrNull() ?: return null
        return rawNum * 100 // Convertir a centavos
    }

    private fun mapPaymentMethod(bank: BankEntity): PaymentMethod {
        return when (bank) {
            BankEntity.BANCOLOMBIA -> PaymentMethod.BANCOLOMBIA
            BankEntity.NEQUI -> PaymentMethod.NEQUI
            BankEntity.DAVIPLATA -> PaymentMethod.DAVIPLATA
            BankEntity.NU -> PaymentMethod.NU
            BankEntity.LULO -> PaymentMethod.LULO
            else -> PaymentMethod.OTHER
        }
    }
}
