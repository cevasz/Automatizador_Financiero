package com.finanzas.automatica.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class AppThemePalette(val displayName: String) {
    // El nombre del enum (KIVO_CORAL) se mantiene sin cambios -- es la clave que ya
    // está guardada en SharedPreferences en dispositivos existentes (ThemePreferences),
    // renombrarlo perdería la preferencia guardada de quien ya tenga la app instalada.
    // Solo cambia el displayName visible, que ahora sí describe la paleta real.
    KIVO_CORAL("Kivo Barro"),
    OCEAN_TEAL("Oceano Teal"),
    FOREST_GREEN("Bosque Verde"),
    MIDNIGHT_BLUE("Medianoche"),
    SUNSET_AMBER("Atardecer")
}

// 1. Kivo Barro (tonos tierra: ladrillo apagado + ocre + oliva)
private val CoralLightColorScheme = lightColorScheme(
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
    error = Color(0xFFE53E3E),
    errorContainer = Color(0xFFFBE4E4)
)

private val CoralDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD08468),
    onPrimary = Color(0xFF3D1A12),
    primaryContainer = FinancePrimaryDark,
    onPrimaryContainer = Color(0xFFF5DDD3),
    secondary = Color(0xFFA3B37F),
    onSecondary = Color(0xFF23301A),
    secondaryContainer = Color(0xFF3A4A2C),
    tertiary = Color(0xFFE0B366),
    tertiaryContainer = Color(0xFF4A3510),
    background = FinanceDarkBackground,
    onBackground = FinanceDarkOnSurface,
    surface = FinanceDarkSurface,
    onSurface = FinanceDarkOnSurface,
    surfaceVariant = FinanceDarkSurfaceVariant,
    onSurfaceVariant = FinanceDarkOnSurfaceVariant,
    outline = Color(0xFF8A7A5C),
    outlineVariant = Color(0xFF453824),
    error = Color(0xFFE7A0A0),
    errorContainer = Color(0xFF5B2F34)
)

// 2. Ocean Teal
private val OceanTealLightColorScheme = lightColorScheme(
    primary = OceanTealPrimary,
    onPrimary = OceanTealOnPrimary,
    primaryContainer = OceanTealPrimaryContainer,
    secondary = OceanTealSecondary,
    tertiary = OceanTealTertiary,
    background = OceanTealBackground,
    onBackground = NeutralOnSurfaceLight,
    surface = OceanTealSurface,
    onSurface = NeutralOnSurfaceLight,
    surfaceVariant = NeutralSurfaceVariantLight,
    onSurfaceVariant = NeutralOnSurfaceVariantLight,
    outline = NeutralOutlineLight,
    outlineVariant = NeutralOutlineVariantLight,
    error = SharedError,
    errorContainer = SharedErrorContainer,
)

private val OceanTealDarkColorScheme = darkColorScheme(
    primary = OceanTealPrimaryContainer,
    onPrimary = Color.Black,
    primaryContainer = OceanTealPrimary,
    secondary = OceanTealSecondary,
    tertiary = OceanTealTertiary,
    background = OceanTealDarkBackground,
    onBackground = NeutralOnSurfaceDark,
    surface = OceanTealDarkSurface,
    onSurface = NeutralOnSurfaceDark,
    surfaceVariant = NeutralSurfaceVariantDark,
    onSurfaceVariant = NeutralOnSurfaceVariantDark,
    outline = NeutralOutlineDark,
    outlineVariant = NeutralOutlineVariantDark,
    error = SharedErrorDark,
    errorContainer = SharedErrorContainerDark,
)

// 3. Forest Green
private val ForestGreenLightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = ForestGreenOnPrimary,
    primaryContainer = ForestGreenPrimaryContainer,
    secondary = ForestGreenSecondary,
    tertiary = ForestGreenTertiary,
    background = ForestGreenBackground,
    onBackground = NeutralOnSurfaceLight,
    surface = ForestGreenSurface,
    onSurface = NeutralOnSurfaceLight,
    surfaceVariant = NeutralSurfaceVariantLight,
    onSurfaceVariant = NeutralOnSurfaceVariantLight,
    outline = NeutralOutlineLight,
    outlineVariant = NeutralOutlineVariantLight,
    error = SharedError,
    errorContainer = SharedErrorContainer,
)

private val ForestGreenDarkColorScheme = darkColorScheme(
    primary = ForestGreenPrimaryContainer,
    onPrimary = Color.Black,
    primaryContainer = ForestGreenPrimary,
    secondary = ForestGreenSecondary,
    tertiary = ForestGreenTertiary,
    background = ForestGreenDarkBackground,
    onBackground = NeutralOnSurfaceDark,
    surface = ForestGreenDarkSurface,
    onSurface = NeutralOnSurfaceDark,
    surfaceVariant = NeutralSurfaceVariantDark,
    onSurfaceVariant = NeutralOnSurfaceVariantDark,
    outline = NeutralOutlineDark,
    outlineVariant = NeutralOutlineVariantDark,
    error = SharedErrorDark,
    errorContainer = SharedErrorContainerDark,
)

// 4. Midnight Blue
private val MidnightBlueLightColorScheme = lightColorScheme(
    primary = MidnightBluePrimary,
    onPrimary = MidnightBlueOnPrimary,
    primaryContainer = MidnightBluePrimaryContainer,
    secondary = MidnightBlueSecondary,
    tertiary = MidnightBlueTertiary,
    background = MidnightBlueBackground,
    onBackground = NeutralOnSurfaceLight,
    surface = MidnightBlueSurface,
    onSurface = NeutralOnSurfaceLight,
    surfaceVariant = NeutralSurfaceVariantLight,
    onSurfaceVariant = NeutralOnSurfaceVariantLight,
    outline = NeutralOutlineLight,
    outlineVariant = NeutralOutlineVariantLight,
    error = SharedError,
    errorContainer = SharedErrorContainer,
)

private val MidnightBlueDarkColorScheme = darkColorScheme(
    primary = MidnightBluePrimaryContainer,
    onPrimary = Color.Black,
    primaryContainer = MidnightBluePrimary,
    secondary = MidnightBlueSecondary,
    tertiary = MidnightBlueTertiary,
    background = MidnightBlueDarkBackground,
    onBackground = NeutralOnSurfaceDark,
    surface = MidnightBlueDarkSurface,
    onSurface = NeutralOnSurfaceDark,
    surfaceVariant = NeutralSurfaceVariantDark,
    onSurfaceVariant = NeutralOnSurfaceVariantDark,
    outline = NeutralOutlineDark,
    outlineVariant = NeutralOutlineVariantDark,
    error = SharedErrorDark,
    errorContainer = SharedErrorContainerDark,
)

// 5. Sunset Amber
private val SunsetAmberLightColorScheme = lightColorScheme(
    primary = SunsetAmberPrimary,
    onPrimary = SunsetAmberOnPrimary,
    primaryContainer = SunsetAmberPrimaryContainer,
    secondary = SunsetAmberSecondary,
    tertiary = SunsetAmberTertiary,
    background = SunsetAmberBackground,
    onBackground = NeutralOnSurfaceLight,
    surface = SunsetAmberSurface,
    onSurface = NeutralOnSurfaceLight,
    surfaceVariant = NeutralSurfaceVariantLight,
    onSurfaceVariant = NeutralOnSurfaceVariantLight,
    outline = NeutralOutlineLight,
    outlineVariant = NeutralOutlineVariantLight,
    error = SharedError,
    errorContainer = SharedErrorContainer,
)

private val SunsetAmberDarkColorScheme = darkColorScheme(
    primary = SunsetAmberPrimaryContainer,
    onPrimary = Color.Black,
    primaryContainer = SunsetAmberPrimary,
    secondary = SunsetAmberSecondary,
    tertiary = SunsetAmberTertiary,
    background = SunsetAmberDarkBackground,
    onBackground = NeutralOnSurfaceDark,
    surface = SunsetAmberDarkSurface,
    onSurface = NeutralOnSurfaceDark,
    surfaceVariant = NeutralSurfaceVariantDark,
    onSurfaceVariant = NeutralOnSurfaceVariantDark,
    outline = NeutralOutlineDark,
    outlineVariant = NeutralOutlineVariantDark,
    error = SharedErrorDark,
    errorContainer = SharedErrorContainerDark,
)

@Composable
fun FinanzasAutomaticaTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    palette: AppThemePalette = AppThemePalette.KIVO_CORAL,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when (palette) {
        AppThemePalette.KIVO_CORAL -> if (darkTheme) CoralDarkColorScheme else CoralLightColorScheme
        AppThemePalette.OCEAN_TEAL -> if (darkTheme) OceanTealDarkColorScheme else OceanTealLightColorScheme
        AppThemePalette.FOREST_GREEN -> if (darkTheme) ForestGreenDarkColorScheme else ForestGreenLightColorScheme
        AppThemePalette.MIDNIGHT_BLUE -> if (darkTheme) MidnightBlueDarkColorScheme else MidnightBlueLightColorScheme
        AppThemePalette.SUNSET_AMBER -> if (darkTheme) SunsetAmberDarkColorScheme else SunsetAmberLightColorScheme
    }

    // Sincroniza los iconos de la barra de estado/navegación con el tema activo.
    // Antes quedaban fijos (oscuros) en themes.xml sin importar el modo elegido,
    // así que en modo oscuro se volvían ilegibles (iconos oscuros sobre fondo oscuro).
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FinanzasTypography,
        content = content
    )
}
