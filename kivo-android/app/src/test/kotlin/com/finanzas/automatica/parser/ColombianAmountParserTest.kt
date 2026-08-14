package com.finanzas.automatica.parser

import com.finanzas.automatica.domain.parser.ColombianAmountParser
import org.junit.jupiter.api.Test

class ColombianAmountParserTest {

    @Test
    fun `whole peso amount with thousands separator`() {
        assert(ColombianAmountParser.toCents("50.000") == 5_000_000L)
        assert(ColombianAmountParser.toCents("1.200.000") == 120_000_000L)
    }

    @Test
    fun `whole peso amount without separators`() {
        assert(ColombianAmountParser.toCents("50000") == 5_000_000L)
    }

    @Test
    fun `US-style thousands grouping with comma is not mistaken for a decimal`() {
        // "100,000" son cien mil pesos (agrupación de miles estilo US), no 100 pesos con 000 decimales.
        assert(ColombianAmountParser.toCents("100,000") == 10_000_000L)
    }

    @Test
    fun `single decimal digit is preserved, not merged into the integer part`() {
        // Antes del fix: "50.000,5" -> se limpiaban '.' y ',' y quedaba el entero "500005",
        // que multiplicado por 100 daba 50000500 (10x de más) en vez de 5000050.
        assert(ColombianAmountParser.toCents("50.000,5") == 5_000_050L)
    }

    @Test
    fun `two decimal digits are preserved, not merged into the integer part`() {
        assert(ColombianAmountParser.toCents("50.000,00") == 5_000_000L)
        assert(ColombianAmountParser.toCents("1.234.567,89") == 123_456_789L)
    }

    @Test
    fun `blank or non-numeric input returns null`() {
        assert(ColombianAmountParser.toCents("") == null)
        assert(ColombianAmountParser.toCents("abc") == null)
    }
}
