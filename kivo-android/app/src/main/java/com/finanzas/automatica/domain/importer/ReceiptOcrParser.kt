package com.finanzas.automatica.domain.importer

import com.finanzas.automatica.domain.model.InvoiceItem
import com.finanzas.automatica.domain.parser.ColombianAmountParser
import java.util.Locale
import java.util.regex.Pattern

/**
 * Convierte el texto plano que devuelve el OCR de una factura/recibo fotografiado en un
 * nombre de comercio + lista de productos editable. Igual que el resto del motor de
 * clasificacion del proyecto, esto es 100% reglas + regex (ver CLAUDE.md) -- ningun LLM
 * interpreta el recibo.
 *
 * El resultado es un borrador: como el OCR de un recibo real es ruidoso (renglones
 * cortados, totales/impuestos que parecen productos, etc.), el usuario siempre revisa y
 * edita los items en InvoiceScreen antes de guardar -- igual que ya pasaba con la plantilla
 * de ejemplo que existia antes de esto.
 */
object ReceiptOcrParser {

    // Palabras de renglones de un recibo colombiano que NO son productos: si aparecen,
    // se descarta el renglon aunque tenga forma de "texto + monto".
    private val NON_PRODUCT_KEYWORDS = listOf(
        "total", "subtotal", "iva", "cambio", "vuelto", "efectivo", "tarjeta",
        "gracias", "nit", "factura", "fecha", "hora", "caja", "cajero", "vendedor",
        "resolucion", "resolución", "consecutivo", "documento", "cliente", "descuento",
        "propina", "servicio", "impuesto", "www.", "http", "telefono", "teléfono",
        "direccion", "dirección", "cufe", "autorizacion", "autorización"
    )

    // "Nombre del producto  <precio>" -- el precio va al final del renglon, separado por
    // uno o mas espacios (asi imprime la mayoria de impresoras de recibos POS).
    private val PRICE_LINE = Pattern.compile("^(.{2,40}?)\\s+\\$?\\s*([\\d.,]{3,})$")

    // "2 x Producto" o "2x Producto" al inicio del nombre -> cantidad + nombre limpio.
    private val QTY_PREFIX = Pattern.compile("^(\\d{1,2})\\s*[xX]\\s+(.+)$")

    // Un producto real casi nunca cuesta menos de $100 COP -- filtra ruido de OCR
    // (numeros de linea, horas mal leidas, etc.) que por casualidad tiene forma de monto.
    private const val MIN_PLAUSIBLE_PRICE_CENTS = 100_00L

    fun parse(ocrText: String): Pair<String, List<InvoiceItem>> {
        val lines = ocrText.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) return "" to emptyList()

        val merchantName = guessMerchantName(lines)
        val items = mutableListOf<InvoiceItem>()

        for (line in lines) {
            val lower = line.lowercase(Locale.getDefault())
            if (NON_PRODUCT_KEYWORDS.any { lower.contains(it) }) continue

            val matcher = PRICE_LINE.matcher(line)
            if (!matcher.matches()) continue

            val price = ColombianAmountParser.toCents(matcher.group(2)) ?: continue
            if (price < MIN_PLAUSIBLE_PRICE_CENTS) continue

            var name = matcher.group(1).trim().trim('-', '.', '·')
            if (name.isBlank()) continue

            var quantity = 1
            val qtyMatcher = QTY_PREFIX.matcher(name)
            if (qtyMatcher.matches()) {
                quantity = qtyMatcher.group(1).toIntOrNull()?.coerceAtLeast(1) ?: 1
                name = qtyMatcher.group(2).trim()
            }
            if (name.isBlank()) continue

            items.add(
                InvoiceItem(
                    productName = name,
                    quantity = quantity,
                    unitPrice = if (quantity > 1) price / quantity else price,
                    totalPrice = price,
                    isDebt = false
                )
            )
        }

        return merchantName to items
    }

    /**
     * El nombre del comercio suele estar en uno de los primeros renglones del recibo
     * (encabezado del ticket), con letras y pocos o ningun digito. Si ninguno cumple ese
     * patron, se usa el primer renglon como mejor intento.
     */
    private fun guessMerchantName(lines: List<String>): String {
        for (line in lines.take(5)) {
            val letters = line.count { it.isLetter() }
            val digits = line.count { it.isDigit() }
            if (letters >= 3 && digits <= 2) return line
        }
        return lines.first()
    }
}
