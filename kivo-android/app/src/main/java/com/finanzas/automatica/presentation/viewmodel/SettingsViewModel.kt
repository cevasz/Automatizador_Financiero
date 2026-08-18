package com.finanzas.automatica.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.entity.AppNotificationEntity
import com.finanzas.automatica.data.repository.AppNotificationRepository
import com.finanzas.automatica.data.repository.DefaultCategories
import com.finanzas.automatica.data.sync.Tombstones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.finanzas.automatica.presentation.ui.theme.ThemeMode
import com.finanzas.automatica.presentation.ui.theme.AppThemePalette
import com.finanzas.automatica.presentation.ui.theme.ThemePreferences

class SettingsViewModel(
    private val database: FinanzasDatabase,
    context: Context
) : ViewModel() {

    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val notifications = AppNotificationRepository(database)

    init {
        // Idempotente: la primera instancia lo inicializa, el resto solo lo reutiliza.
        ThemePreferences.init(appContext)
    }

    private val _notificationsEnabled = kotlinx.coroutines.flow.MutableStateFlow(true)
    val notificationsEnabled: kotlinx.coroutines.flow.StateFlow<Boolean> = _notificationsEnabled

    private val _biometricEnabled = kotlinx.coroutines.flow.MutableStateFlow(false)
    val biometricEnabled: kotlinx.coroutines.flow.StateFlow<Boolean> = _biometricEnabled

    private val _autoConfirmHighConfidence = kotlinx.coroutines.flow.MutableStateFlow(true)
    val autoConfirmHighConfidence: kotlinx.coroutines.flow.StateFlow<Boolean> = _autoConfirmHighConfidence

    private val _isContributor = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isContributor: kotlinx.coroutines.flow.StateFlow<Boolean> = _isContributor

    private val _contributionAmount = kotlinx.coroutines.flow.MutableStateFlow(0)
    val contributionAmount: kotlinx.coroutines.flow.StateFlow<Int> = _contributionAmount

    private val _showOnboarding = kotlinx.coroutines.flow.MutableStateFlow(true)
    val showOnboarding: kotlinx.coroutines.flow.StateFlow<Boolean> = _showOnboarding

    private val _exportDataFormat = kotlinx.coroutines.flow.MutableStateFlow("CSV")
    val exportDataFormat: kotlinx.coroutines.flow.StateFlow<String> = _exportDataFormat

    // Delegado a ThemePreferences (singleton): así todas las instancias de
    // SettingsViewModel comparten el mismo estado en vivo en vez de cada una tener
    // su propio StateFlow desincronizado (ver docs de ThemePreferences).
    val themeMode: kotlinx.coroutines.flow.StateFlow<ThemeMode> = ThemePreferences.themeMode
    val themePalette: kotlinx.coroutines.flow.StateFlow<AppThemePalette> = ThemePreferences.themePalette

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _notificationsEnabled.value = preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        _biometricEnabled.value = preferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        _autoConfirmHighConfidence.value = preferences.getBoolean(KEY_AUTO_CONFIRM_HIGH_CONFIDENCE, true)
        _isContributor.value = preferences.getBoolean(KEY_IS_CONTRIBUTOR, false)
        _contributionAmount.value = preferences.getInt(KEY_CONTRIBUTION_AMOUNT, 0)
        _showOnboarding.value = preferences.getBoolean(KEY_SHOW_ONBOARDING, true)
        _exportDataFormat.value = preferences.getString(KEY_EXPORT_FORMAT, "CSV") ?: "CSV"
        // El modo y la paleta de tema ya se cargan en ThemePreferences.init(); no hay
        // estado local que sincronizar aquí.
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        persistBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        _biometricEnabled.value = enabled
        persistBoolean(KEY_BIOMETRIC_ENABLED, enabled)
    }

    fun setAutoConfirmHighConfidence(enabled: Boolean) {
        _autoConfirmHighConfidence.value = enabled
        persistBoolean(KEY_AUTO_CONFIRM_HIGH_CONFIDENCE, enabled)
    }

    fun setContributorStatus(isContributor: Boolean, amount: Int) {
        _isContributor.value = isContributor
        _contributionAmount.value = amount
        preferences.edit()
            .putBoolean(KEY_IS_CONTRIBUTOR, isContributor)
            .putInt(KEY_CONTRIBUTION_AMOUNT, amount)
            .apply()
    }

    fun setShowOnboarding(show: Boolean) {
        _showOnboarding.value = show
        persistBoolean(KEY_SHOW_ONBOARDING, show)
    }

    fun setExportFormat(format: String) {
        val normalized = format.trim().uppercase(Locale.getDefault())
        _exportDataFormat.value = normalized
        preferences.edit()
            .putString(KEY_EXPORT_FORMAT, normalized)
            .apply()
    }

    fun setThemeMode(mode: ThemeMode) {
        ThemePreferences.setThemeMode(mode)
    }

    fun setThemePalette(palette: AppThemePalette) {
        ThemePreferences.setThemePalette(palette)
    }

    /**
     * Exporta los movimientos en el formato elegido por el usuario en Ajustes.
     * Antes esta funcion ignoraba _exportDataFormat.value y siempre escribia un JSON
     * completo, sin importar si el selector decia CSV, Excel o PDF -- y nunca avisaba
     * al usuario del resultado (ni exito ni error). Ahora CSV realmente exporta CSV;
     * Excel/PDF (roadmap, ver docs/PENDIENTES.md) avisan honestamente que aun no estan
     * disponibles en vez de entregar un archivo con el formato equivocado.
     */
    fun exportData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (_exportDataFormat.value) {
                    "CSV" -> {
                        val exportDir = File(appContext.filesDir, "exports").apply { mkdirs() }
                        val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
                        val file = File(exportDir, "kivo-movimientos-$timestamp.csv")
                        file.writeText(buildMovementsCsv())
                        Log.i(TAG, "Exportacion CSV completada: ${file.absolutePath}")
                        notifications.notify(
                            type = AppNotificationEntity.TYPE_SYSTEM,
                            title = "Exportación lista",
                            message = "Tus movimientos se guardaron en ${file.name}."
                        )
                    }
                    else -> {
                        Log.i(TAG, "Exportacion a ${_exportDataFormat.value} solicitada (aun no implementada)")
                        notifications.notify(
                            type = AppNotificationEntity.TYPE_SYSTEM,
                            title = "Formato aún no disponible",
                            message = "La exportación a ${_exportDataFormat.value} está en el roadmap. Usa CSV por ahora."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error exportando datos", e)
                notifications.notify(
                    type = AppNotificationEntity.TYPE_SYSTEM,
                    title = "Error al exportar",
                    message = "No se pudo completar la exportación. Intenta de nuevo."
                )
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Primero las lapidas de todo. Sin esto, "borrar mis datos" dejaria
                // la app vacia y la siguiente sincronizacion la repoblaria entera
                // desde la nube — es decir, el boton no borraria nada de verdad.
                Tombstones(database).antesDeBorrarTodo()

                database.movementDao().deleteAll()
                database.budgetDao().deleteAll()
                database.savingsGoalDao().deleteAll()
                database.classificationRuleDao().deleteAll()
                database.agendaDao().deleteAll()
                database.categoryDao().deleteAll()
                DefaultCategories.seed(database)
                Log.i(TAG, "Datos locales eliminados y categorias por defecto restauradas")
            } catch (e: Exception) {
                Log.e(TAG, "Error eliminando datos locales", e)
            }
        }
    }

    private fun persistBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    private suspend fun buildMovementsCsv(): String {
        val movements = database.movementDao().getAll()
        val categoryNames = database.categoryDao().getAll().associateBy({ it.id }, { it.name })
        val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withZone(java.time.ZoneId.systemDefault())

        val header = "fecha,tipo,monto_cop,medio_pago,contraparte,categoria,banco,estado"
        val rows = movements.joinToString("\n") { movement ->
            listOf(
                dateFormatter.format(java.time.Instant.ofEpochMilli(movement.date)),
                movement.type,
                (movement.amount / 100.0).toString(),
                movement.paymentMethod,
                movement.counterpartyRaw.csvEscape(),
                (movement.categoryId?.let { categoryNames[it] } ?: "Sin categoria").csvEscape(),
                movement.bankEntity,
                movement.confirmationState
            ).joinToString(",")
        }
        return if (rows.isBlank()) header else "$header\n$rows"
    }

    private fun String.csvEscape(): String {
        return if (contains(",") || contains("\"") || contains("\n")) {
            "\"" + replace("\"", "\"\"") + "\""
        } else {
            this
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
        private const val PREFS_NAME = "finanzas_settings"

        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_AUTO_CONFIRM_HIGH_CONFIDENCE = "auto_confirm_high_confidence"
        private const val KEY_IS_CONTRIBUTOR = "is_contributor"
        private const val KEY_CONTRIBUTION_AMOUNT = "contribution_amount"
        private const val KEY_SHOW_ONBOARDING = "show_onboarding"
        private const val KEY_EXPORT_FORMAT = "export_format"
        // El modo/paleta de tema ahora vive en ThemePreferences (mismas keys, mismo
        // archivo "finanzas_settings"), compartido entre todas las instancias de este ViewModel.
    }
}
