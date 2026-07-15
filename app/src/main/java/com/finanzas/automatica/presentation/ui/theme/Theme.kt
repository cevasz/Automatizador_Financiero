package com.finanzas.automatica.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    primaryContainer = TerracottaSecondary.copy(alpha = 0.2f),
    secondary = Ocre,
    secondaryContainer = OcreLight.copy(alpha = 0.2f),
    tertiary = TerracottaDark,
    surface = OcreSurface,
    background = OcreSurface,
    onPrimary = TerracottaOnPrimary,
    onSecondary = OcreOnSurface,
    onSurface = OcreOnSurface,
    onBackground = OcreOnSurface,
    error = Color(0xFFB00020),
    outline = Color(0xFF757575)
)

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaSecondary,
    primaryContainer = TerracottaDark,
    secondary = OcreLight,
    secondaryContainer = Ocre,
    tertiary = TerracottaSecondary,
    surface = SurfaceDark,
    background = SurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onSurface = Color.White,
    onBackground = Color.White,
    error = Color(0xFFCF6679),
    outline = Color(0xFFBDBDBD)
)

@Composable
fun FinanzasAutomaticaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}