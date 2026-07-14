package com.finanzas.automatica.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta terracota-ocre (tema "fundador")
private val TerracottaPrimary = Color(0xFF8D6E63)
private val TerracottaOnPrimary = Color.White
private val TerracottaSecondary = Color(0xFFA1887F)
private val TerracottaTertiary = Color(0xFFBCAAA4)
private val OcreSurface = Color(0xFFFDF8F5)
private val OcreOnSurface = Color(0xFF3E2723)

val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = TerracottaOnPrimary,
    secondary = TerracottaSecondary,
    tertiary = TerracottaTertiary,
    surface = OcreSurface,
    onSurface = OcreOnSurface,
    background = OcreSurface,
    onBackground = OcreOnSurface,
    error = Color(0xFFC62828),
    onError = Color.White
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBCAAA4),
    onPrimary = Color(0xFF3E2723),
    secondary = Color(0xFFA1887F),
    tertiary = Color(0xFF8D6E63),
    surface = Color(0xFF4E342E),
    onSurface = Color(0xFFFDF8F5),
    background = Color(0xFF3E2723),
    onBackground = Color(0xFFFDF8F5),
    error = Color(0xFFEF9A9A),
    onError = Color(0xFF3E2723)
)

@Composable
fun FinanzasTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}