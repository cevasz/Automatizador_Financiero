package com.finanzas.automatica.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Terracota = Color(0xFF8D6E63)
private val TerracotaLight = Color(0xFFA1887F)
private val TerracotaDark = Color(0xFF6D4C41)
private val Ocre = Color(0xFFD4A843)
private val OcreLight = Color(0xFFE6C87A)
private val SurfaceLight = Color(0xFFFDFBF7)
private val SurfaceDark = Color(0xFF1E1E1E)

private val LightColorScheme = lightColorScheme(
    primary = Terracota,
    primaryContainer = TerracotaLight.copy(alpha = 0.2f),
    secondary = Ocre,
    secondaryContainer = OcreLight.copy(alpha = 0.2f),
    tertiary = TerracotaDark,
    surface = SurfaceLight,
    background = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onSurface = Color.Black,
    onBackground = Color.Black,
    error = Color(0xFFB00020),
    outline = Color(0xFF757575)
)

private val DarkColorScheme = darkColorScheme(
    primary = TerracotaLight,
    primaryContainer = TerracotaDark,
    secondary = OcreLight,
    secondaryContainer = Ocre,
    tertiary = TerracotaLight,
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