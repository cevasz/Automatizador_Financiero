package com.finanzas.automatica.importer

import com.finanzas.automatica.domain.importer.PdfStatementExtractor
import com.finanzas.automatica.domain.importer.StatementImporter
import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import org.junit.jupiter.api.Test

class PdfStatementExtractorTest {

    @Test
    fun `extract statement text from real PDF fixture and classify it`() {
        val fixture = javaClass.classLoader
            .getResourceAsStream("fixtures/extracto_bancolombia.pdf")!!
            .use { it.readBytes() }

        val text = PdfStatementExtractor.extractText(fixture)
        assert(text.contains("Transferencia LUIS RINCON"))

        // El texto extraído se alimenta al mismo motor de parseo/clasificación.
        val summary = StatementImporter.parseStatementText(text, BankEntity.BANCOLOMBIA)

        assert(summary.totalCount == 3)
        assert(summary.incomeCount == 1)
        assert(summary.expenseCount == 2)
        assert(summary.totalIncomeAmount == 10000000L) // $100.000 COP en centavos
        assert(summary.totalExpenseAmount == 16500000L) // $165.000 COP en centavos

        val first = summary.importedMovements.first()
        assert(first.type == MovementType.INCOME)
        assert(first.amount == 10000000L)
        assert(first.counterpartyRaw == "Transferencia LUIS RINCON")
    }
}
