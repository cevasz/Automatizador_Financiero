package com.finanzas.automatica.importer

import com.finanzas.automatica.domain.importer.ReceiptOcrParser
import org.junit.jupiter.api.Test

/**
 * A diferencia de los fixtures de [com.finanzas.automatica.parser.BankParserTest] (texto
 * REAL copiado de notificaciones bancarias, ver CLAUDE.md), no hay forma de conseguir una
 * foto real de un recibo en este entorno de desarrollo. El texto de abajo es una
 * reconstruccion sintetica pero representativa de lo que ML Kit Text Recognition suele
 * devolver al leer un tiquete POS colombiano tipico (encabezado, items con precio al
 * final del renglon, subtotal/iva/total/efectivo/cambio) -- documentado explicitamente
 * como no-real para que quede claro que este test cubre la forma del parseo, no un caso
 * verificado contra hardware real.
 */
class ReceiptOcrParserTest {

    @Test
    fun `parses items from a typical Colombian POS receipt`() {
        val ocrText = """
            SUPERMERCADO EXITO
            NIT 890903938-8
            Cra 50 45 12 Medellin
            --------------------------
            2 x Leche Alqueria 900ml   9.800
            Pan Bimbo Tajado             6.500
            1 x Huevos AA x30           18.900
            --------------------------
            SUBTOTAL                    35.200
            IVA                             0
            TOTAL                       35.200
            EFECTIVO                    40.000
            CAMBIO                       4.800
            GRACIAS POR SU COMPRA
        """.trimIndent()

        val (merchantName, items) = ReceiptOcrParser.parse(ocrText)

        assert(merchantName == "SUPERMERCADO EXITO") { "merchant: $merchantName" }
        assert(items.size == 3) { "items found: ${items.map { it.productName }}" }

        val leche = items.first { it.productName.contains("Leche") }
        assert(leche.quantity == 2)
        assert(leche.totalPrice == 980000L) // $9.800 COP en centavos
        assert(leche.unitPrice == 490000L) // $9.800 / 2

        val pan = items.first { it.productName.contains("Pan") }
        assert(pan.quantity == 1)
        assert(pan.totalPrice == 650000L) // $6.500 COP en centavos

        val huevos = items.first { it.productName.contains("Huevos") }
        assert(huevos.totalPrice == 1890000L) // $18.900 COP en centavos

        // Los totales/impuestos/efectivo/cambio NO deben colarse como productos.
        assert(items.none { it.productName.contains("TOTAL", ignoreCase = true) })
        assert(items.none { it.productName.contains("EFECTIVO", ignoreCase = true) })
        assert(items.none { it.productName.contains("CAMBIO", ignoreCase = true) })
    }

    @Test
    fun `blank OCR text yields no items`() {
        val (merchantName, items) = ReceiptOcrParser.parse("")
        assert(merchantName.isEmpty())
        assert(items.isEmpty())
    }

    @Test
    fun `lines without a plausible trailing price are ignored`() {
        val ocrText = """
            Tienda La Esquina
            Bienvenido a nuestra tienda
            Sigue nuestras redes sociales
        """.trimIndent()

        val (_, items) = ReceiptOcrParser.parse(ocrText)
        assert(items.isEmpty())
    }
}
