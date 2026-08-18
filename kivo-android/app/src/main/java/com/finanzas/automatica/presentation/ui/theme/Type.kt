package com.finanzas.automatica.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.finanzas.automatica.R

/**
 * Tipografia de Kivo.
 *
 * Antes toda la app usaba `FontFamily.Default` (Roboto) con cuatro pesos
 * distintos, y `SemiBold` aparecia 60 veces: cuando casi todo es semibold, nada
 * destaca — era la razon principal de que las pantallas se vieran planas. Ahora
 * hay **dos familias con un papel claro cada una y dos pesos**:
 *
 *  - **Fraunces** (serif) solo para titulos de pantalla. Es lo que le da caracter
 *    y lo que conecta visualmente con el panel web, que ya usa serif en sus
 *    encabezados. Nunca lleva numeros.
 *  - **Manrope** (sans) para todo lo demas: cuerpo, etiquetas y **dinero**.
 *
 * Las dos son variables (un archivo por familia, todos los pesos dentro), lo que
 * pesa menos que empaquetar una instancia estatica por peso.
 *
 * ## Por que Manrope y no la fuente del sistema
 * Manrope trae la caracteristica OpenType `tnum` (cifras tabulares) — verificado
 * en el archivo, no asumido. Sin ella, cada digito tiene un ancho distinto y los
 * montos **se mueven** al actualizarse: un saldo que pasa de $1.111 a $8.888
 * cambia de ancho y empuja lo que tiene al lado. En una app cuyo contenido
 * principal son cifras, ese temblor es lo que separa "se ve hecho a mano" de "se
 * ve serio". Ver [KivoText.amount].
 */

private val manrope = R.font.manrope_variable
private val fraunces = R.font.fraunces_variable

/** Solo dos pesos en toda la app: cuerpo y enfasis. Ver KDoc de arriba. */
private val Regular = FontWeight.W400
private val Emphasis = FontWeight.W600

// FontVariation sigue marcada como experimental en Compose, pero es la unica via
// para instanciar pesos de una fuente variable; la alternativa seria empaquetar un
// archivo estatico por peso y pesar mas.
@OptIn(ExperimentalTextApi::class)
private fun variable(resId: Int, weight: FontWeight) =
    Font(resId, weight = weight, variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)))

val ManropeFamily = FontFamily(
    variable(manrope, Regular),
    variable(manrope, Emphasis)
)

val FrauncesFamily = FontFamily(
    // Un solo peso: el serif es para titulos, y un titulo no necesita dos pesos
    // para leerse como titulo — ya se distingue por familia y tamaño.
    variable(fraunces, Emphasis)
)

/**
 * Estilos que no encajan en los slots de Material 3, para cosas propias de Kivo.
 */
object KivoText {

    /**
     * **El estilo del dinero.** `tnum` fija el ancho de cada digito, asi que un
     * monto no cambia de tamaño al actualizarse ni desalinea una columna de
     * cifras. Usar siempre este estilo (o [amountLarge]) para montos.
     */
    val amount = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = Emphasis,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.01).sp,
        fontFeatureSettings = "tnum"
    )

    /** Monto protagonista: saldo del mes, total de una meta. */
    val amountLarge = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = Emphasis,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.02).sp,
        fontFeatureSettings = "tnum"
    )

    /** Monto secundario: filas de lista, totales pequeños. */
    val amountSmall = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = Emphasis,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontFeatureSettings = "tnum"
    )

    /**
     * Etiqueta de seccion en mayusculas con tracking abierto. Da jerarquia sin
     * gastar otro peso tipografico ni otro tamaño grande.
     */
    val eyebrow = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = Emphasis,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.9.sp
    )
}

val FinanzasTypography = Typography(
    // --- Titulos de pantalla: serif -----------------------------------------
    headlineLarge = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = Emphasis,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = Emphasis,
        fontSize = 27.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.015).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = Emphasis,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.01).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = Emphasis,
        fontSize = 21.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.01).sp
    ),

    // --- Interfaz: sans ------------------------------------------------------
    titleMedium = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = Emphasis,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = Emphasis,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = Regular,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = Regular,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = Regular,
        fontSize = 13.sp,
        lineHeight = 19.sp
    ),
    labelLarge = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = Emphasis,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = Emphasis,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = Emphasis,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    )
)
