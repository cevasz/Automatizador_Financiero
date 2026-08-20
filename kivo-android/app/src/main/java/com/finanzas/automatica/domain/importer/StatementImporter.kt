package com.finanzas.automatica.domain.importer

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementSource
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.RawMovement
import com.finanzas.automatica.domain.parser.ColombianAmountParser
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        defaultBank: BankEntity = BankEntity.BANCOLOMBIA,
        source: MovementSource = MovementSource.NOTIFICATION
    ): ImportSummary {
        val lines = text.lines()
        val movements = mutableListOf<RawMovement>()

        // Los extractos de Bancolombia fechan cada renglon como "1/04", sin año: el
        // año vive una sola vez, en el encabezado. Se lee antes de recorrer las
        // lineas para poder completar cada fecha.
        val period = detectPeriod(text)

        // Si el extracto marca los egresos con un menos, el signo es DATO y la
        // palabra de la descripcion es una suposicion. Medido sobre un extracto
        // real: 5 renglones de "PAGO DE ..." y "Reverso COMPRA ..." son ingresos y
        // el criterio por palabras los volteaba, descuadrando el reparto en 3,2
        // millones aunque la suma total cuadrara. Solo se recurre a las palabras
        // cuando el documento no usa signos (extractos de debito y credito en
        // columnas separadas).
        val usesSigns = SIGNED_AMOUNT_REGEX.containsMatchIn(text)

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isBlank() || trimmed.startsWith("#") || trimmed.startsWith("//")) continue

            val movement = parseLine(trimmed, defaultBank, source, period, usesSigns)
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

    /**
     * Un monto tal como aparece en un extracto, en cualquiera de las dos
     * convenciones que conviven en Colombia: la local ("1.234.567,89") y la que
     * usan de hecho los extractos de Bancolombia ("1,234,567.89"). Cual de los dos
     * separadores es el decimal lo decide [ColombianAmountParser] mirando cual va
     * mas a la derecha, asi que aqui basta aceptar ambos.
     *
     * Exige SIEMPRE separador de miles, decimales o simbolo de peso. Un numero
     * pelado no cuenta como monto -- antes si contaba, y el patron encontraba su
     * primera coincidencia dentro de la propia fecha: todo movimiento de 2026 se
     * importaba por $2026. Los numeros de documento caian en la misma trampa.
     *
     * La ultima alternativa cubre los montos menores a un peso que el extracto
     * escribe sin cero delante (".31" en los abonos de intereses).
     */
    private val AMOUNT_REGEX = Regex(
        """[-+]?(?:\$\s*)?\d{1,3}(?:[.,]\d{3})+(?:[.,]\d{1,2})?-?""" +
        """|[-+]?(?:\$\s*)?\d+[.,]\d{1,2}-?""" +
        """|[-+]?\$\s*\d+-?""" +
        """|[-+]?[.,]\d{1,2}-?"""
    )

    /** Un monto que lleva signo explicito, para saber si el extracto los usa. */
    private val SIGNED_AMOUNT_REGEX = Regex("""[-+]\s*\d""")

    /** Fecha completa en cualquier punto de la linea. */
    private val DATE_REGEX = Regex("""\b\d{1,2}[/-]\d{1,2}[/-]\d{2,4}\b""")

    /**
     * Fecha sin año al principio del renglon ("1/04"), que es como la escribe el
     * extracto de Bancolombia: el año no esta en la fila sino en el encabezado.
     *
     * Se exige al PRINCIPIO de la linea a proposito. Suelta, "1/04" aparece dentro
     * de descripciones y referencias, y bastaria para fabricar movimientos que
     * nadie hizo. El lookahead evita que se coma el prefijo de una fecha completa:
     * sin el, "11/08/2026" se leia como el "11/08" de un extracto sin año. Cubre
     * tambien el digito suelto: con solo `[/-]` el motor retrocedia, dejaba que el
     * mes capturara "0" y la fecha completa pasaba igual, ahora como "11/0".
     */
    private val SHORT_DATE_REGEX = Regex("""^(\d{1,2})[/-](\d{1,2})(?![\d/-])""")

    /** El periodo que declara el encabezado: "DESDE: 2026/03/31 HASTA: 2026/06/30". */
    private val PERIOD_REGEX = Regex(
        """DESDE:?\s*(\d{4})[/-](\d{1,2})[/-](\d{1,2})\s*HASTA:?\s*(\d{4})[/-](\d{1,2})[/-](\d{1,2})""",
        RegexOption.IGNORE_CASE
    )

    private fun parseLine(
        line: String,
        defaultBank: BankEntity,
        source: MovementSource,
        period: StatementPeriod?,
        usesSigns: Boolean
    ): RawMovement? {
        parseCsvLine(line, defaultBank, source)?.let { return it }
        return parseFreeTextLine(line, defaultBank, source, period, usesSigns)
    }

    /**
     * Periodo declarado en el encabezado del extracto. Sirve para ponerle el año a
     * las fechas que vienen sin el.
     */
    private data class StatementPeriod(val start: LocalDate, val end: LocalDate) {
        /**
         * El año que hace que dia/mes caiga dentro del periodo, o null si ninguno lo
         * hace. Se prueba año por año porque un extracto puede cruzar diciembre: en
         * un periodo 2025/12/01-2026/01/31, "15/12" es de 2025 y "05/01" de 2026.
         */
        fun resolve(day: Int, month: Int): LocalDate? {
            for (year in start.year..end.year) {
                val candidate = try {
                    LocalDate.of(year, month, day)
                } catch (_: Exception) {
                    continue
                }
                if (!candidate.isBefore(start) && !candidate.isAfter(end)) return candidate
            }
            return null
        }
    }

    private fun detectPeriod(text: String): StatementPeriod? {
        val m = PERIOD_REGEX.find(text) ?: return null
        return try {
            StatementPeriod(
                LocalDate.of(m.groupValues[1].toInt(), m.groupValues[2].toInt(), m.groupValues[3].toInt()),
                LocalDate.of(m.groupValues[4].toInt(), m.groupValues[5].toInt(), m.groupValues[6].toInt())
            )
        } catch (_: Exception) {
            null
        }
    }

    /**
     * CSV de tres o mas columnas (Fecha, Descripcion, Monto).
     *
     * Se reconoce por que la PRIMERA columna es, ella sola, una fecha valida -- no
     * por contar separadores. Contarlos rompia los extractos reales: en Colombia la
     * coma es el separador decimal, asi que un renglon con valor y saldo
     * ("45.900,00   1.234.567,00") ya trae dos comas, se tomaba por CSV de tres
     * columnas y se descartaba entero al no poder leer la fecha.
     */
    private fun parseCsvLine(line: String, defaultBank: BankEntity, source: MovementSource): RawMovement? {
        val tokens = if (line.contains(";")) line.split(";") else line.split(",")
        if (tokens.size < 3) return null

        val dateStr = tokens[0].trim().removeSurrounding("\"")
        val date = parseDateStr(dateStr) ?: return null

        val descStr = tokens[1].trim().removeSurrounding("\"")
        val amountStr = tokens[2].trim().removeSurrounding("\"")
        val amountParsed = parseAmountVal(amountStr) ?: return null

        return RawMovement(
            type = deduceType(amountStr, descStr),
            amount = Math.abs(amountParsed),
            paymentMethod = mapPaymentMethod(defaultBank),
            counterpartyRaw = descStr.ifBlank { defaultBank.name },
            date = date,
            bankEntity = defaultBank,
            rawText = line,
            confidence = 0.95,
            source = source
        )
    }

    /**
     * Renglon de extracto en texto libre, tal como lo entrega PDFBox al aplanar la
     * tabla: `1/04 TRANSFERENCIA DESDE NEQUI 105,000.00 391,917.51`.
     *
     * Cuando el renglon trae dos montos, el ULTIMO es el saldo acumulado y el
     * anterior el valor del movimiento. Quedarse con el ultimo registraria el saldo
     * de la cuenta como si fuera una compra -- y en un extracto real eso pasa en
     * TODOS los renglones, porque todos llevan columna de saldo.
     */
    private fun parseFreeTextLine(
        line: String,
        defaultBank: BankEntity,
        source: MovementSource,
        period: StatementPeriod?,
        usesSigns: Boolean
    ): RawMovement? {
        val (date, dateText) = resolveDate(line, period) ?: return null

        // La fecha se quita antes de buscar montos: sus digitos no deben competir.
        val resto = line.replaceFirst(dateText, " ")

        val amounts = AMOUNT_REGEX.findAll(resto).toList()
        if (amounts.isEmpty()) return null
        val chosen = if (amounts.size >= 2) amounts[amounts.size - 2] else amounts[0]

        val amountParsed = parseAmountVal(chosen.value) ?: return null

        // Se quitan TODOS los montos, no solo el elegido: el saldo acumulado no
        // describe nada y ensucia la contraparte.
        val desc = AMOUNT_REGEX.replace(resto, " ")
            .replace(Regex("""[$;]"""), "")
            .replace(Regex("""\s{2,}"""), " ")
            .trim()
            .ifBlank { defaultBank.name }

        return RawMovement(
            type = deduceType(chosen.value, line, usesSigns),
            amount = Math.abs(amountParsed),
            paymentMethod = mapPaymentMethod(defaultBank),
            counterpartyRaw = desc,
            date = date,
            bankEntity = defaultBank,
            rawText = line,
            confidence = 0.90,
            source = source
        )
    }

    /**
     * Fecha del renglon, junto al texto exacto que la representa para poder quitarla.
     *
     * Si el renglon empieza por una fecha sin año y el extracto no declara periodo,
     * devuelve null en vez de suponer el año en curso: una fecha inventada mete el
     * movimiento en un mes que no le toca y descuadra los totales sin avisar.
     */
    private fun resolveDate(line: String, period: StatementPeriod?): Pair<Instant, String>? {
        SHORT_DATE_REGEX.find(line)?.let { corta ->
            val fecha = period?.resolve(corta.groupValues[1].toInt(), corta.groupValues[2].toInt())
                ?: return null
            return fecha.atStartOfDay(ZoneId.systemDefault()).toInstant() to corta.value
        }
        val completa = DATE_REGEX.find(line) ?: return null
        val fecha = parseDateStr(completa.value) ?: return null
        return fecha to completa.value
    }

    /**
     * El signo manda sobre las palabras: un extracto marca el egreso en el monto
     * (delante o detras, ambas convenciones se usan) y la descripcion puede decir
     * "pago" en un ingreso perfectamente valido -- "PAGO DE NOMINA", por ejemplo.
     */
    private fun deduceType(
        amountRaw: String,
        contexto: String,
        usesSigns: Boolean = false
    ): MovementType {
        if (amountRaw.startsWith("-") || amountRaw.endsWith("-")) return MovementType.EXPENSE
        if (amountRaw.startsWith("+")) return MovementType.INCOME

        // El documento marca signos y este monto no lleva ninguno: es un ingreso, y
        // que la descripcion diga "pago" no lo cambia.
        if (usesSigns) return MovementType.INCOME

        val lower = contexto.lowercase(Locale.getDefault())
        val esEgreso = listOf("compra", "pago", "retiro", "debito", "débito", "cuota", "impuesto")
            .any { lower.contains(it) }
        return if (esEgreso) MovementType.EXPENSE else MovementType.INCOME
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
        // El signo se quita antes de convertir, no despues. Algunos extractos marcan
        // el egreso con un menos AL FINAL ("120.500,00-"), y ese caracter colado
        // detras de los decimales hacia que el parser dejara de ver ",00" como parte
        // decimal y lo tomara por separador de miles: el monto salia 100 veces mayor.
        // El tipo de movimiento ya se decidio con el signo en deduceType.
        val clean = amountStr
            .replace("$", "")
            .replace(" ", "")
            .trim('-', '+')
        return ColombianAmountParser.toCents(clean)
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
