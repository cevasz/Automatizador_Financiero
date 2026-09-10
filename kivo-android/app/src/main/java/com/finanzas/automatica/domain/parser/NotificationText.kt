package com.finanzas.automatica.domain.parser

import java.text.Normalizer
import java.util.Locale

/**
 * Normaliza el texto de una notificación antes de aplicarle reglas.
 *
 * Las notificaciones bancarias reales cambian de redacción todo el tiempo y llegan con
 * emojis, mayúsculas inconsistentes, tildes a veces sí y a veces no ("devolución" /
 * "devolucion"), saltos de línea y espacios dobles porque el texto se arma pegando
 * título + cuerpo + líneas. Comparar contra ese texto crudo obligaba a duplicar cada
 * palabra con y sin tilde y hacía que un emoji o un salto de línea pegado a la palabra
 * rompiera el `contains`. Todas las reglas del léxico se escriben entonces sobre el
 * texto ya normalizado: minúsculas, sin diacríticos, sin emojis y con espacios simples.
 *
 * El texto ORIGINAL se conserva aparte: la contraparte ("LUIS RINCON") y el monto se
 * extraen de él para no perder mayúsculas ni el formato del número.
 */
object NotificationText {

    private val DIACRITICS = Regex("\\p{Mn}+")

    // Emojis y símbolos decorativos. No toca \p{Sc} (símbolos de moneda) para que el "$"
    // siga estando disponible en el texto normalizado.
    private val DECORATIVE = Regex("[\\p{So}\\p{Cf}\\p{Co}]+")

    private val WHITESPACE = Regex("\\s+")

    fun normalize(raw: String): String {
        val decomposed = Normalizer.normalize(raw, Normalizer.Form.NFD)
        val withoutAccents = DIACRITICS.replace(decomposed, "")
        val withoutEmojis = DECORATIVE.replace(withoutAccents, " ")
        return WHITESPACE.replace(withoutEmojis, " ")
            .trim()
            .lowercase(Locale.ROOT)
    }
}
