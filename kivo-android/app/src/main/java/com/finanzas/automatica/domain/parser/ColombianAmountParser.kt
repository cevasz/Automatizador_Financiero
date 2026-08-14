package com.finanzas.automatica.domain.parser

/**
 * Utilidad compartida para convertir montos en formato colombiano (COP) a centavos.
 *
 * Convención colombiana: el punto (.) es separador de miles y la coma (,) es separador
 * decimal (ej: "$1.234.567,89"). El código anterior (duplicado en [BaseBankParser] y en
 * `StatementImporter`) quitaba puntos y comas sin distinguir cuál actuaba como separador
 * decimal, así que un monto con decimales (ej: "$50.000,5") se leía como el entero
 * "500005" y, al multiplicarlo por 100 para convertir a centavos, el valor guardado
 * quedaba inflado 10x (o ~100x con dos decimales). Los montos siempre se almacenan en
 * centavos (ver CLAUDE.md / modelos `Movement`, `RawMovement`, `MovementEntity`).
 */
object ColombianAmountParser {

    /**
     * Convierte un monto de texto a centavos COP, o `null` si no contiene dígitos válidos.
     *
     * Soporta agrupación de miles con punto ("50.000"), decimales con coma ("50.000,50",
     * "1.234.567,89"), agrupación estilo US con coma ("100,000") y montos sin separadores
     * ("50000"). El separador final solo se interpreta como decimal si aparece una única
     * vez y va seguido de exactamente 1 o 2 dígitos — de lo contrario se asume que es
     * separador de miles.
     */
    fun toCents(raw: String): Long? {
        val lastComma = raw.lastIndexOf(',')
        val lastDot = raw.lastIndexOf('.')

        var integerPart = raw
        var decimalPart = "00"

        if (lastComma != -1 || lastDot != -1) {
            // El separador que aparece más a la derecha es el candidato a decimal.
            val decimalSepChar = when {
                lastComma == -1 -> '.'
                lastDot == -1 -> ','
                lastComma > lastDot -> ','
                else -> '.'
            }
            val decimalSepIndex = raw.lastIndexOf(decimalSepChar)
            val trailing = raw.substring(decimalSepIndex + 1)
            val appearsOnce = raw.count { it == decimalSepChar } == 1

            if (trailing.length in 1..2 && trailing.all { it.isDigit() } && appearsOnce) {
                decimalPart = trailing
                integerPart = raw.substring(0, decimalSepIndex)
            }
        }

        val cleanInt = integerPart.filter { it.isDigit() }
        if (cleanInt.isEmpty()) return null

        val decimalDigits = decimalPart.padEnd(2, '0').take(2)
        return try {
            cleanInt.toLong() * 100 + decimalDigits.toLong()
        } catch (e: NumberFormatException) {
            null
        }
    }
}
