package com.finanzas.automatica.importer

import com.finanzas.automatica.domain.importer.StatementImporter
import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import org.junit.jupiter.api.Test

class StatementImporterTest {

    @Test
    fun `parse CSV statement lines successfully`() {
        val csvData = """
            11/08/2026, Transferencia LUIS RINCON, 100000
            08/08/2026, Compra Supermercado Exito, -45000
            01/08/2026, Pago Servicios EPM, -120000
            25/07/2026, Abono de Nomina, 2500000
        """.trimIndent()

        val summary = StatementImporter.parseStatementText(csvData, BankEntity.BANCOLOMBIA)

        assert(summary.totalCount == 4)
        assert(summary.incomeCount == 2)
        assert(summary.expenseCount == 2)
        assert(summary.totalIncomeAmount == 260000000L) // $2.600.000 COP en centavos
        assert(summary.totalExpenseAmount == 16500000L) // $165.000 COP en centavos

        val first = summary.importedMovements.first()
        assert(first.type == MovementType.INCOME)
        assert(first.amount == 10000000L) // $100.000 COP
        assert(first.counterpartyRaw == "Transferencia LUIS RINCON")
    }

    @Test
    fun `CSV amount with decimals is not inflated 10x`() {
        // Regresión: antes del fix, "50000,50" perdía la coma decimal al limpiar el
        // string y quedaba como el entero "5000050", que al multiplicar por 100 para
        // convertir a centavos daba 500005000 (10x de más) en vez de 5000050.
        // Se usa ";" como delimitador porque el monto en sí contiene una coma decimal.
        val csvData = "11/08/2026;Pago con decimales;-50000,50"

        val summary = StatementImporter.parseStatementText(csvData, BankEntity.BANCOLOMBIA)

        assert(summary.totalCount == 1)
        val movement = summary.importedMovements.first()
        assert(movement.type == MovementType.EXPENSE)
        assert(movement.amount == 5_000_050L) { "expected 5000050, got ${movement.amount}" }
    }
}
