package com.finanzas.automatica.importer

import com.finanzas.automatica.domain.importer.StatementImporter
import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import org.junit.jupiter.api.Test
import java.time.ZoneId

/**
 * Formato real del extracto de Bancolombia, tal como PDFBox lo entrega con
 * `sortByPosition`. La estructura (fecha sin año, dos montos por renglon, coma de
 * miles y punto decimal, menos delante en los egresos) esta copiada de un extracto
 * de verdad; los importes y los nombres estan cambiados.
 *
 * Verificado ademas contra el extracto original completo: 739 de 739 renglones, y
 * los totales de ingresos y egresos coinciden al centavo con TOTAL ABONOS y TOTAL
 * CARGOS del encabezado.
 */
class StatementImporterBancolombiaTest {

    private val extracto = """
        ESTADO DE CUENTA
        DESDE: 2026/03/31 HASTA: 2026/06/30
        CUENTA DE AHORROS
        SALDO ANTERIOR ${'$'} 286,917.51
        FECHA DESCRIPCIÓN SUCURSAL DCTO. VALOR SALDO
        1/04 TRANSFERENCIA DESDE OTRO BANCO 105,000.00 391,917.51
        1/04 ABONO INTERESES AHORROS .31 391,917.82
        1/04 COMPRA EN TIENDA EJEMPLO -9,400.00 382,517.82
        2/04 RETIRO CORRESPONSAL CB CANAL CORRESPONSA -50,000.00 332,517.82
        3/04 PAGO DE UN TERCERO 40,000.00 372,517.82
    """.trimIndent()

    @Test
    fun `importa todos los renglones del extracto`() {
        val s = StatementImporter.parseStatementText(extracto, BankEntity.BANCOLOMBIA)
        assert(s.totalCount == 5) { "importados ${s.totalCount} de 5" }
    }

    @Test
    fun `toma el valor del movimiento y no el saldo acumulado`() {
        // Cada renglon lleva dos montos: VALOR y SALDO. Quedarse con el ultimo
        // registraria el saldo de la cuenta como si fuera una compra, y eso pasa en
        // todos los renglones porque todos llevan columna de saldo.
        val m = StatementImporter.parseStatementText(extracto, BankEntity.BANCOLOMBIA)
            .importedMovements.first()

        assert(m.amount == 10_500_000L) { "monto=${m.amount}, se esperaba el valor y no el saldo" }
        assert(m.counterpartyRaw == "TRANSFERENCIA DESDE OTRO BANCO") { "desc='${m.counterpartyRaw}'" }
    }

    @Test
    fun `el año sale del periodo del encabezado`() {
        // El renglon dice "1/04" y no trae año: vive una sola vez, en el encabezado.
        val m = StatementImporter.parseStatementText(extracto, BankEntity.BANCOLOMBIA)
            .importedMovements.first()

        val fecha = m.date.atZone(ZoneId.systemDefault()).toLocalDate()
        assert(fecha.year == 2026) { "año=${fecha.year}" }
        assert(fecha.monthValue == 4 && fecha.dayOfMonth == 1) { "fecha=$fecha" }
    }

    @Test
    fun `sin periodo declarado no se inventa el año`() {
        // Una fecha inventada mete el movimiento en un mes que no le toca y descuadra
        // los totales sin avisar. Mejor no importar el renglon.
        val sinEncabezado = "1/04 TRANSFERENCIA DESDE OTRO BANCO 105,000.00 391,917.51"

        val s = StatementImporter.parseStatementText(sinEncabezado, BankEntity.BANCOLOMBIA)

        assert(s.totalCount == 0) { "se invento un año para ${s.totalCount} movimientos" }
    }

    @Test
    fun `un monto menor a un peso no descarta el renglon`() {
        // Regresion: los abonos de intereses vienen como ".31", sin cero delante. El
        // conversor exigia digitos en la parte entera y devolvia null, asi que el
        // renglon entero se perdia -- 64 de 739 en el extracto real.
        val m = StatementImporter.parseStatementText(extracto, BankEntity.BANCOLOMBIA)
            .importedMovements[1]

        assert(m.amount == 31L) { "monto=${m.amount}, se esperaban 31 centavos" }
        assert(m.type == MovementType.INCOME)
    }

    @Test
    fun `el signo del extracto manda sobre las palabras de la descripcion`() {
        // Regresion: "PAGO DE ..." y "Reverso COMPRA ..." son ingresos, y el criterio
        // por palabras los volteaba. Eran 5 renglones en el extracto real y bastaban
        // para descuadrar el reparto en 3,2 millones aunque la suma total cuadrara.
        val movimientos = StatementImporter.parseStatementText(extracto, BankEntity.BANCOLOMBIA)
            .importedMovements

        val pago = movimientos.last()
        assert(pago.counterpartyRaw.startsWith("PAGO DE")) { "desc='${pago.counterpartyRaw}'" }
        assert(pago.type == MovementType.INCOME) { "quedo como ${pago.type} pese a no tener signo" }

        assert(movimientos[2].type == MovementType.EXPENSE) { "la compra con menos debe ser egreso" }
        assert(movimientos[2].amount == 940_000L)
    }

    @Test
    fun `lee la convencion de miles con coma y decimales con punto`() {
        // Bancolombia escribe "105,000.00", no "105.000,00". Las dos conviven en
        // Colombia y el conversor decide por cual separador va mas a la derecha.
        val m = StatementImporter.parseStatementText(extracto, BankEntity.BANCOLOMBIA)
            .importedMovements[3]

        assert(m.amount == 5_000_000L) { "monto=${m.amount}" }
        assert(m.type == MovementType.EXPENSE)
    }
}
