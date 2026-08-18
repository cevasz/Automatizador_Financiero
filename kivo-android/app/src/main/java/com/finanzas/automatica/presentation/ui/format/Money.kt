package com.finanzas.automatica.presentation.ui.format

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

/**
 * El unico formateador de dinero de la app.
 *
 * Antes cada pantalla construia el suyo: cinco definiciones identicas de
 * `NumberFormat.getCurrencyInstance(Locale("es","CO"))` repartidas en cinco
 * archivos, y 19 divisiones sueltas — unas `/ 100` (division entera, que
 * **trunca**) y otras `/ 100.0`. Dos consecuencias: el dinero podia verse
 * distinto segun la pantalla, y cualquier ajuste habia que hacerlo cinco veces.
 *
 * Todas las funciones reciben **centavos** (`Long`), que es como se guarda el
 * monto en Room y en Postgres. Recibir centavos y no pesos elimina de raiz la
 * pregunta "¿esto ya venia dividido?", que es exactamente el error que producia
 * las dos variantes de division.
 */
object Money {

    private val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        // Sin decimales: en pesos colombianos los centavos no existen en la
        // practica y solo agregan ruido a cada cifra de la pantalla.
        maximumFractionDigits = 0
    }

    /** `5_000_000` centavos → `"$ 50.000"`. */
    fun format(cents: Long): String = synchronized(formatter) {
        // NumberFormat no es seguro entre hilos y esta instancia es compartida.
        formatter.format(cents / 100.0)
    }

    /** Con signo explicito segun el tipo de movimiento: `"+ $ 50.000"` / `"− $ 50.000"`. */
    fun formatSigned(cents: Long, isIncome: Boolean): String =
        "${if (isIncome) "+" else "−"} ${format(abs(cents))}"

    /**
     * Version corta para graficas y espacios estrechos: `"$ 1,2 M"`.
     * En una etiqueta de eje, `"$ 1.234.567"` no cabe y se corta a la mitad.
     */
    fun compact(cents: Long): String {
        val pesos = abs(cents) / 100.0
        val signo = if (cents < 0) "−" else ""
        val co = Locale("es", "CO")
        return when {
            pesos >= 1_000_000_000 -> signo + "$ " + "%.1f MM".format(co, pesos / 1_000_000_000)
            pesos >= 1_000_000 -> signo + "$ " + "%.1f M".format(co, pesos / 1_000_000)
            pesos >= 1_000 -> signo + "$ " + "%.0f K".format(co, pesos / 1_000)
            else -> format(cents)
        }
    }

    /**
     * Convierte lo que el usuario escribe (en pesos, con o sin separadores) a
     * centavos. Devuelve null si no hay ningun numero utilizable, para que la
     * pantalla pueda avisar en vez de guardar un cero silencioso.
     */
    fun parsePesos(entrada: String): Long? {
        val limpio = entrada.filter { it.isDigit() }
        if (limpio.isEmpty()) return null
        return limpio.toLongOrNull()?.times(100)
    }
}
