package com.finanzas.automatica.importer

import com.finanzas.automatica.domain.importer.StatementImporter
import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import org.junit.jupiter.api.Test

/**
 * Renglones de extracto en texto libre: lo que entrega PDFBox al aplanar la tabla
 * de un PDF, que es como llegan los extractos de verdad.
 *
 * Los casos de [StatementImporterTest] son CSV con comas, un formato que ningun
 * banco produce; por eso los dos fallos que cubre esta clase pasaron inadvertidos.
 */
class StatementImporterFreeTextTest {

    @Test
    fun `renglon con valor y saldo no se descarta`() {
        // Regresion: se decidia "esto es CSV" contando comas. En Colombia la coma es
        // el separador decimal, asi que un renglon con valor Y saldo trae dos y se
        // tomaba por CSV de tres columnas; al no poder leer "11/08/2026  COMPRA
        // EXITO CHAPINERO   45.900" como fecha, la linea se descartaba entera.
        // Como los extractos traen saldo en cada renglon, no se importaba nada.
        val linea = "11/08/2026  COMPRA EXITO CHAPINERO        45.900,00      1.234.567,00"

        val summary = StatementImporter.parseStatementText(linea, BankEntity.BANCOLOMBIA)

        assert(summary.totalCount == 1) { "se descarto la linea" }
        val m = summary.importedMovements.first()
        assert(m.amount == 4_590_000L) { "se importo el saldo y no el valor: ${m.amount}" }
        assert(m.counterpartyRaw == "COMPRA EXITO CHAPINERO") { "desc='${m.counterpartyRaw}'" }
    }

    @Test
    fun `el ano de la fecha no se confunde con el monto`() {
        // Regresion: el patron de monto aceptaba cualquier numero de 4+ digitos y su
        // primera coincidencia caia dentro de la propia fecha. Todo movimiento de
        // 2026 se importaba por $2026 y el monto real quedaba en la descripcion.
        val linea = "11/08/2026 Transferencia LUIS RINCON \$100.000"

        val m = StatementImporter.parseStatementText(linea, BankEntity.BANCOLOMBIA)
            .importedMovements.first()

        assert(m.amount == 10_000_000L) { "monto=${m.amount}, se esperaba 10000000" }
        assert(m.type == MovementType.INCOME)
    }

    @Test
    fun `el signo explicito manda sobre las palabras de la descripcion`() {
        // "PAGO DE NOMINA" es un ingreso pese a decir "pago". Cuando el extracto
        // marca el signo, el signo decide: si la palabra pesara mas, un sueldo
        // entraria como gasto.
        val texto = """
            11/08/2026  PAGO DE NOMINA ACME SAS         +2.500.000,00
            12/08/2026  RETIRO CAJERO CALLE 45           -80.000,00
            13/08/2026  PAGO PSE CLARO                   120.500,00-
        """.trimIndent()

        val summary = StatementImporter.parseStatementText(texto, BankEntity.BANCOLOMBIA)

        assert(summary.totalCount == 3) { "se importaron ${summary.totalCount}" }
        val m = summary.importedMovements
        assert(m[0].type == MovementType.INCOME) { "nomina quedo como ${m[0].type}" }
        assert(m[0].amount == 250_000_000L) { "nomina monto=${m[0].amount}" }
        assert(m[1].type == MovementType.EXPENSE) { "retiro quedo como ${m[1].type}" }
        assert(m[1].amount == 8_000_000L) { "retiro monto=${m[1].amount}" }
        assert(m[2].type == MovementType.EXPENSE) { "pse quedo como ${m[2].type}" }
        assert(m[2].amount == 12_050_000L) { "pse monto=${m[2].amount}" }
    }

    @Test
    fun `una linea sin monto reconocible no inventa un movimiento`() {
        // Encabezados y pies de pagina comparten el texto con los movimientos. Antes
        // cualquier numero largo servia de monto, asi que un numero de cuenta o una
        // fecha bastaban para fabricar un movimiento que nadie hizo.
        val texto = """
            EXTRACTO DE CUENTA DE AHORROS 12345678901
            Periodo 01/08/2026 al 31/08/2026
            Pagina 1 de 3
        """.trimIndent()

        val summary = StatementImporter.parseStatementText(texto, BankEntity.BANCOLOMBIA)

        assert(summary.totalCount == 0) { "se inventaron ${summary.totalCount} movimientos" }
    }
}
