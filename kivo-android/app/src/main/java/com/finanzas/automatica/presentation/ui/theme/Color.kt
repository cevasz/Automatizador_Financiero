package com.finanzas.automatica.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// Kivo Theme - Coral vibrante sobre crema cálido, con acentos teal y ámbar
val FinancePrimary = Color(0xFFF56565) // Coral Kivo
val FinancePrimaryDark = Color(0xFF7A2E2E)
val FinanceOnPrimary = Color(0xFFFFFFFF)
val FinancePrimaryContainer = Color(0xFFFC8181) // Coral claro
val FinanceOnPrimaryContainer = Color(0xFF5C1A1A)

val FinanceSecondary = Color(0xFF2C7A7B) // Teal complementario
val FinanceSecondaryContainer = Color(0xFFD8ECEA)
val FinanceTertiary = Color(0xFFD69E2E) // Ámbar cálido
val FinanceTertiaryContainer = Color(0xFFF8EBCB)

val FinanceBackground = Color(0xFFFEFCF5) // Crema cálido
val FinanceSurface = Color(0xFFFFFFFF)
val FinanceSurfaceVariant = Color(0xFFF3EFE6)
val FinanceOnSurface = Color(0xFF2D3748) // Pizarra profunda
val FinanceOnSurfaceVariant = Color(0xFF5A626E)
val FinanceOutline = Color(0xFFD8D4CA)
val FinanceOutlineSoft = Color(0xFFE8E4DA)

val FinanceDarkBackground = Color(0xFF14161C)
val FinanceDarkSurface = Color(0xFF1C1F27)
val FinanceDarkSurfaceVariant = Color(0xFF2A2E38)
val FinanceDarkOnSurface = Color(0xFFE8EBF2)
val FinanceDarkOnSurfaceVariant = Color(0xFFA8AEB8)

val IncomeGreen = Color(0xFF2C7A7B) // Teal de ingresos
val ExpenseRose = Color(0xFFF56565) // Coral de gastos
val WarningAmber = Color(0xFFD69E2E) // Ámbar Kivo
val InfoBlue = Color(0xFF3182CE)

val TerracottaPrimary = FinancePrimary
val TerracottaOnPrimary = FinanceOnPrimary
val TerracottaSecondary = FinanceSecondary
val TerracottaTertiary = FinanceTertiary
val OcreSurface = FinanceBackground
val OcreOnSurface = FinanceOnSurface

val Ocre = WarningAmber
val OcreLight = Color(0xFFF8EBCB)
val TerracottaDark = FinancePrimaryDark
val SurfaceLight = FinanceSurface
val SurfaceDark = FinanceDarkSurface

// Ocean Teal Colors
val OceanTealPrimary = Color(0xFF006D77)
val OceanTealOnPrimary = Color(0xFFFFFFFF)
val OceanTealPrimaryContainer = Color(0xFF83C5BE)
val OceanTealSecondary = Color(0xFFE29578)
val OceanTealTertiary = Color(0xFFFFDDD2)
val OceanTealBackground = Color(0xFFF8F9FA)
val OceanTealSurface = Color(0xFFFFFFFF)
val OceanTealDarkBackground = Color(0xFF1A1F24)
val OceanTealDarkSurface = Color(0xFF222831)

// Forest Green Colors
val ForestGreenPrimary = Color(0xFF2D6A4F)
val ForestGreenOnPrimary = Color(0xFFFFFFFF)
val ForestGreenPrimaryContainer = Color(0xFF74C69D)
val ForestGreenSecondary = Color(0xFF1B4332)
val ForestGreenTertiary = Color(0xFF95D5B2)
val ForestGreenBackground = Color(0xFFF4F8F5)
val ForestGreenSurface = Color(0xFFFFFFFF)
val ForestGreenDarkBackground = Color(0xFF0F1A15)
val ForestGreenDarkSurface = Color(0xFF15261E)

// Midnight Blue Colors
val MidnightBluePrimary = Color(0xFF1E3A8A)
val MidnightBlueOnPrimary = Color(0xFFFFFFFF)
val MidnightBluePrimaryContainer = Color(0xFF93C5FD)
val MidnightBlueSecondary = Color(0xFF3B82F6)
val MidnightBlueTertiary = Color(0xFFBFDBFE)
val MidnightBlueBackground = Color(0xFFF0F9FF)
val MidnightBlueSurface = Color(0xFFFFFFFF)
val MidnightBlueDarkBackground = Color(0xFF0F172A)
val MidnightBlueDarkSurface = Color(0xFF1E293B)

// Sunset Amber Colors
val SunsetAmberPrimary = Color(0xFFD97706)
val SunsetAmberOnPrimary = Color(0xFFFFFFFF)
val SunsetAmberPrimaryContainer = Color(0xFFFCD34D)
val SunsetAmberSecondary = Color(0xFFB45309)
val SunsetAmberTertiary = Color(0xFFFDE68A)
val SunsetAmberBackground = Color(0xFFFFFBEB)
val SunsetAmberSurface = Color(0xFFFFFFFF)
val SunsetAmberDarkBackground = Color(0xFF291900)
val SunsetAmberDarkSurface = Color(0xFF3D2600)

// Roles neutros compartidos por las 4 paletas alternativas (Ocean Teal, Forest Green,
// Midnight Blue, Sunset Amber). Solo primary/secondary/tertiary cambian por paleta;
// los roles neutros (texto, bordes, variantes de superficie, error) se mantienen
// consistentes entre paletas, igual que en la mayoría de sistemas Material3 con
// múltiples temas de marca. Antes estos roles no se definían y Compose los rellenaba
// con el morado por defecto de Material3, que no combinaba con ninguna paleta.
val NeutralOnSurfaceLight = Color(0xFF23262B)
val NeutralOnSurfaceVariantLight = Color(0xFF5C6066)
val NeutralSurfaceVariantLight = Color(0xFFECECEC)
val NeutralOutlineLight = Color(0xFFC7C7C7)
val NeutralOutlineVariantLight = Color(0xFFE1E1E1)

val NeutralOnSurfaceDark = Color(0xFFE7E7E7)
val NeutralOnSurfaceVariantDark = Color(0xFFC0C0C0)
val NeutralSurfaceVariantDark = Color(0xFF32353A)
val NeutralOutlineDark = Color(0xFF8A8D91)
val NeutralOutlineVariantDark = Color(0xFF45484D)

val SharedError = Color(0xFFE53E3E)
val SharedErrorContainer = Color(0xFFFBE4E4)
val SharedErrorDark = Color(0xFFE7A0A0)
val SharedErrorContainerDark = Color(0xFF5B2F34)
