package com.finanzas.automatica.presentation.ui.theme

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fuente única de verdad para el modo de tema y la paleta seleccionados.
 *
 * `SettingsViewModel` se crea varias veces con instancias independientes a lo largo
 * de la app (una en `MainActivity`, otra por cada pantalla que lo instancia via
 * `databaseViewModel { SettingsViewModel(...) }` en `AppNavHost`), cada una con su
 * propio `MutableStateFlow`. Antes, cambiar el tema en Ajustes solo actualizaba la
 * instancia de esa pantalla — la instancia que `MainActivity` usa para pintar
 * `FinanzasAutomaticaTheme` nunca se enteraba, así que el cambio de tema no se veía
 * hasta reiniciar la app. Al mover el estado a este objeto singleton (con
 * `SharedPreferences` como respaldo), todas las instancias de `SettingsViewModel`
 * leen y escriben el mismo `StateFlow`, así que el cambio se aplica al instante en
 * toda la app.
 */
object ThemePreferences {
    private const val PREFS_NAME = "finanzas_settings"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_THEME_PALETTE = "theme_palette"

    @Volatile
    private var preferences: SharedPreferences? = null

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _themePalette = MutableStateFlow(AppThemePalette.KIVO_CORAL)
    val themePalette: StateFlow<AppThemePalette> = _themePalette.asStateFlow()

    /** Idempotente: puede llamarse desde cada instancia de SettingsViewModel sin problema. */
    fun init(context: Context) {
        if (preferences != null) return
        synchronized(this) {
            if (preferences != null) return
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            preferences = prefs

            val modeStr = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
            _themeMode.value = runCatching { ThemeMode.valueOf(modeStr ?: "") }.getOrDefault(ThemeMode.SYSTEM)

            val paletteStr = prefs.getString(KEY_THEME_PALETTE, AppThemePalette.KIVO_CORAL.name)
            _themePalette.value = runCatching { AppThemePalette.valueOf(paletteStr ?: "") }.getOrDefault(AppThemePalette.KIVO_CORAL)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        preferences?.edit()?.putString(KEY_THEME_MODE, mode.name)?.apply()
    }

    fun setThemePalette(palette: AppThemePalette) {
        _themePalette.value = palette
        preferences?.edit()?.putString(KEY_THEME_PALETTE, palette.name)?.apply()
    }
}
