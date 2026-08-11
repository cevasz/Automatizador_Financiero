package com.finanzas.automatica.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = FinancePrimary,
    onPrimary = FinanceOnPrimary,
    primaryContainer = FinancePrimaryContainer,
    onPrimaryContainer = FinanceOnPrimaryContainer,
    secondary = FinanceSecondary,
    onSecondary = Color.White,
    secondaryContainer = FinanceSecondaryContainer,
    tertiary = FinanceTertiary,
    tertiaryContainer = FinanceTertiaryContainer,
    background = FinanceBackground,
    onBackground = FinanceOnSurface,
    surface = FinanceSurface,
    onSurface = FinanceOnSurface,
    surfaceVariant = FinanceSurfaceVariant,
    onSurfaceVariant = FinanceOnSurfaceVariant,
    outline = FinanceOutline,
    outlineVariant = FinanceOutlineSoft,
    error = ExpenseRose,
    errorContainer = FinanceTertiaryContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA8CEB4),
    onPrimary = Color(0xFF173422),
    primaryContainer = FinancePrimaryDark,
    onPrimaryContainer = Color(0xFFDCEFE4),
    secondary = Color(0xFFC9BFE5),
    onSecondary = Color(0xFF2F2748),
    secondaryContainer = Color(0xFF41375C),
    tertiary = Color(0xFFE7B9BE),
    tertiaryContainer = Color(0xFF633D42),
    background = FinanceDarkBackground,
    onBackground = FinanceDarkOnSurface,
    surface = FinanceDarkSurface,
    onSurface = FinanceDarkOnSurface,
    surfaceVariant = FinanceDarkSurfaceVariant,
    onSurfaceVariant = FinanceDarkOnSurfaceVariant,
    outline = Color(0xFF7E8A82),
    outlineVariant = Color(0xFF3D4842),
    error = Color(0xFFE7A0A0),
    errorContainer = Color(0xFF5B2F34)
)

@Composable
fun FinanzasAutomaticaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = FinanzasTypography,
        content = content
    )
}
