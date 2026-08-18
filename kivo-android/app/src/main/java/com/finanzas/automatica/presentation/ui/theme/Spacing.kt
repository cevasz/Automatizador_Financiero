package com.finanzas.automatica.presentation.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Reticula de espaciado de Kivo.
 *
 * Antes los valores se elegian uno por uno en cada pantalla: dominaban `8.dp` y
 * `12.dp` (60 y 52 usos), mientras que `24.dp` aparecia 7 veces y `32.dp` solo 3,
 * y se colaban valores sueltos como `3`, `5`, `6`, `10`, `14`, `18`, `22` y
 * `42.dp`. El resultado eran dos problemas a la vez: **todo apretado**, porque el
 * aire real (24-32) casi no se usaba, y **sin ritmo**, porque los valores fuera
 * de la reticula rompen la repeticion sin que se note de donde viene la
 * sensacion de descuido.
 *
 * Toda la escala es multiplo de 4. Los nombres semanticos de abajo existen para
 * que la decision se tome una vez y no en cada pantalla.
 *
 * ## Espaciado por relacion, no por costumbre
 * La distancia comunica jerarquia: lo que va junto se agrupa, lo que no, se
 * separa. Si dos textos relacionados estan a [xs], el salto al siguiente grupo
 * deberia ser al menos el doble. Por eso [betweenGroups] es el doble de
 * [betweenItems] y [betweenSections] el doble de [betweenGroups].
 */
object KivoSpacing {

    // --- Escala base (multiplos de 4) ----------------------------------------
    val none = 0.dp
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 48.dp

    // --- Semanticos ----------------------------------------------------------

    /** Margen lateral de una pantalla. */
    val screen = lg

    /** Relleno interior de una tarjeta. 24 y no 16: es el aire que hace que una
     *  tarjeta se lea como una pieza y no como un bloque de texto con borde. */
    val card = xl

    /** Separacion entre dos lineas del mismo bloque (titulo y subtitulo). */
    val betweenItems = xs

    /** Separacion entre bloques distintos dentro de una misma tarjeta. */
    val betweenGroups = md

    /** Separacion entre secciones de una pantalla. */
    val betweenSections = xxl

    /** Separacion entre tarjetas consecutivas en una lista. */
    val betweenCards = md

    /** Zona de toque minima recomendada (Material: 48dp; 44 es el minimo de iOS). */
    val touchTarget = 48.dp

    /** Ancho maximo util de un dialogo o de una columna de texto en tablet. */
    val dialogMaxWidth = 460.dp

    /**
     * Espacio libre al final de una lista con barra de navegacion o boton
     * flotante encima. Sin esto el ultimo elemento queda tapado y parece que la
     * lista se corto.
     */
    val listBottom = 96.dp
}
