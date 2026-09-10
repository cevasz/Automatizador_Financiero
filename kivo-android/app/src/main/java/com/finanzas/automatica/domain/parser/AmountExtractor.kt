package com.finanzas.automatica.domain.parser

import java.util.regex.Pattern

/**
 * Extrae el monto de la transacción del texto de una notificación.
 *
 * Dos problemas reales que resuelve, y que antes producían montos equivocados:
 *
 * 1. Muchas notificaciones traen MÁS DE UN monto ("Pagaste $15.500 en La 14. Tu saldo es
 *    $1.230.000"). Tomar el primero funciona a veces y falla cuando el banco pone el
 *    saldo, el cupo o el total del mes primero. Aquí se recogen todos los montos con su
 *    posición y se descartan los que vienen acompañados de palabras de saldo/cupo/total.
 * 2. El formato varía: "$ 50.000", "$50,000", "COP 50.000", "50.000 COP", "$50.000,50",
 *    "50000 pesos". Un solo patrón no cubre eso.
 *
 * La conversión a centavos la hace [ColombianAmountParser] (punto = miles, coma =
 * decimales) — los montos siempre se guardan en centavos, ver CLAUDE.md.
 */
object AmountExtractor {

    /** Patrones con marca de moneda explícita ($ / COP / pesos). */
    private val CURRENCY_MARKED: List<Pattern> = listOf(
        // $ 50.000 | $50,000 | COP$ 50.000
        Pattern.compile("(?:cop\\s*)?\\$\\s*(\\d[\\d.,]*)", Pattern.CASE_INSENSITIVE),
        // COP 50.000
        Pattern.compile("\\bcop\\s+(\\d[\\d.,]*)", Pattern.CASE_INSENSITIVE),
        // 50.000 COP | 50.000 pesos
        Pattern.compile("(\\d[\\d.,]*)\\s*(?:cop|pesos)\\b", Pattern.CASE_INSENSITIVE)
    )

    /**
     * Último recurso: número sin marca de moneda, pero solo con forma real de monto
     * (agrupado en miles o al menos 4 dígitos seguidos). Evita capturar horas ("10:30"),
     * porcentajes ("20%") o número de cuotas ("3 cuotas") como si fueran el monto.
     */
    private val BARE_AMOUNT: Pattern =
        Pattern.compile("\\b(\\d{1,3}(?:[.,]\\d{3})+(?:[.,]\\d{1,2})?|\\d{4,})\\b")

    /**
     * Palabras que, cerca de un monto, indican que ese número NO es el valor de la
     * transacción sino un dato de contexto (saldo que queda, cupo disponible, total
     * acumulado del mes, meta de ahorro...).
     */
    private val CONTEXT_NOISE = Regex(
        "\\b(saldo|saldos|disponible|disponibles|cupo|limite|deuda|meta|ahorrad[oa]|" +
            "ahorro|acumulad[oa]|total del mes|total acumulado|quedan|te queda|te quedan|" +
            "restante|minimo|maximo)\\b"
    )

    /** Ventana de texto que se mira alrededor del monto para detectar el ruido de contexto. */
    private const val CONTEXT_BEFORE = 32
    private const val CONTEXT_AFTER = 20

    private data class Candidate(val position: Int, val raw: String, val noisy: Boolean)

    /** `true` si el texto trae al menos un monto con marca de moneda explícita. */
    fun hasCurrencyAmount(text: String): Boolean =
        CURRENCY_MARKED.any { it.matcher(text).find() }

    /**
     * Monto de la transacción en centavos, o `null` si no hay ninguno legible.
     *
     * @param allowBareNumber permite caer al número sin marca de moneda. Solo debería
     *   usarse cuando ya se sabe que la notificación es transaccional.
     */
    fun extractCents(text: String, allowBareNumber: Boolean = true): Long? {
        val candidates = collect(text, CURRENCY_MARKED)
            .ifEmpty { if (allowBareNumber) collect(text, listOf(BARE_AMOUNT)) else emptyList() }
        if (candidates.isEmpty()) return null

        // Preferir el primer monto "limpio"; si todos vienen con contexto de saldo/cupo,
        // usar el primero de todos antes que descartar la notificación entera.
        val ordered = candidates.sortedBy { it.position }
        val chosen = ordered.firstOrNull { !it.noisy } ?: ordered.first()
        return ColombianAmountParser.toCents(chosen.raw)
    }

    private fun collect(text: String, patterns: List<Pattern>): List<Candidate> {
        val found = LinkedHashMap<Int, Candidate>()
        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                val start = matcher.start(1)
                val raw = matcher.group(1) ?: continue
                if (raw.none { it.isDigit() }) continue
                // Un mismo número puede coincidir con varios patrones ("$300.000 COP");
                // la posición del número lo deduplica.
                if (found.containsKey(start)) continue
                found[start] = Candidate(start, raw, isNoisy(text, start, matcher.end(1)))
            }
        }
        return found.values.toList()
    }

    // Corta en el final de la frase anterior/siguiente. Exige espacio despues del signo
    // para no partir en el punto separador de miles ("$1.000.000").
    private val SENTENCE_BREAK = Regex("[.;!?\\n]\\s+")

    private fun isNoisy(text: String, start: Int, end: Int): Boolean {
        // Solo la frase que contiene el monto cuenta como contexto: sin esto, un texto
        // como "Tu saldo es $1.000.000. Pagaste $15.500" marcaba tambien el monto real
        // como ruido porque "saldo" quedaba dentro de la ventana de caracteres.
        val before = text.substring(maxOf(0, start - CONTEXT_BEFORE), start)
            .split(SENTENCE_BREAK).last()
        val after = text.substring(end, minOf(text.length, end + CONTEXT_AFTER))
            .split(SENTENCE_BREAK).first()
        val window = NotificationText.normalize("$before | $after")
        return CONTEXT_NOISE.containsMatchIn(window)
    }
}
