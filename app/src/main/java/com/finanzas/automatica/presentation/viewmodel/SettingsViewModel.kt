package com.finanzas.automatica.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.repository.DefaultCategories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewModel(
    private val database: FinanzasDatabase,
    context: Context
) : ViewModel() {

    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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

    fun exportData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val exportDir = File(appContext.filesDir, "exports").apply { mkdirs() }
                val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
                val file = File(exportDir, "finanzas-export-$timestamp.json")
                file.writeText(buildExportSnapshot().toString(2))
                Log.i(TAG, "Exportacion completada: ${file.absolutePath}")
            } catch (e: Exception) {
                Log.e(TAG, "Error exportando datos", e)
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
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

    private suspend fun buildExportSnapshot(): JSONObject {
        val snapshot = JSONObject()
        snapshot.put("exportedAt", System.currentTimeMillis())
        snapshot.put("settings", buildSettingsObject())
        snapshot.put("movements", buildMovementsArray())
        snapshot.put("agendaEntries", buildAgendaArray())
        snapshot.put("categories", buildCategoriesArray())
        snapshot.put("budgets", buildBudgetsArray())
        snapshot.put("savingsGoals", buildSavingsGoalsArray())
        snapshot.put("classificationRules", buildClassificationRulesArray())
        return snapshot
    }

    private fun buildSettingsObject(): JSONObject {
        return JSONObject()
            .put("notificationsEnabled", _notificationsEnabled.value)
            .put("biometricEnabled", _biometricEnabled.value)
            .put("autoConfirmHighConfidence", _autoConfirmHighConfidence.value)
            .put("isContributor", _isContributor.value)
            .put("contributionAmount", _contributionAmount.value)
            .put("showOnboarding", _showOnboarding.value)
            .put("exportDataFormat", _exportDataFormat.value)
    }

    private suspend fun buildMovementsArray(): JSONArray {
        return database.movementDao().getAll().toJsonArray { movement ->
            JSONObject()
                .put("id", movement.id)
                .put("type", movement.type)
                .put("amount", movement.amount)
                .put("paymentMethod", movement.paymentMethod)
                .put("counterpartyRaw", movement.counterpartyRaw)
                .put("counterpartyId", movement.counterpartyId ?: JSONObject.NULL)
                .put("categoryId", movement.categoryId ?: JSONObject.NULL)
                .put("date", movement.date)
                .put("source", movement.source)
                .put("confirmationState", movement.confirmationState)
                .put("bankEntity", movement.bankEntity)
                .put("rawText", movement.rawText)
                .put("createdAt", movement.createdAt)
                .put("updatedAt", movement.updatedAt)
        }
    }

    private suspend fun buildAgendaArray(): JSONArray {
        return database.agendaDao().getAll().toJsonArray { entry ->
            JSONObject()
                .put("id", entry.id)
                .put("accountIdentifier", entry.accountIdentifier)
                .put("displayName", entry.displayName)
                .put("defaultCategoryId", entry.defaultCategoryId ?: JSONObject.NULL)
                .put("color", entry.color)
                .put("origin", entry.origin)
                .put("createdAt", entry.createdAt)
                .put("updatedAt", entry.updatedAt)
        }
    }

    private suspend fun buildCategoriesArray(): JSONArray {
        return database.categoryDao().getAll().toJsonArray { category ->
            JSONObject()
                .put("id", category.id)
                .put("name", category.name)
                .put("type", category.type)
                .put("iconName", category.iconName)
                .put("isCustom", category.isCustom)
                .put("parentCategoryId", category.parentCategoryId ?: JSONObject.NULL)
                .put("sortOrder", category.sortOrder)
                .put("createdAt", category.createdAt)
        }
    }

    private suspend fun buildBudgetsArray(): JSONArray {
        return database.budgetDao().getAll().toJsonArray { budget ->
            JSONObject()
                .put("id", budget.id)
                .put("categoryId", budget.categoryId)
                .put("monthlyLimit", budget.monthlyLimit)
                .put("month", budget.month)
                .put("year", budget.year)
                .put("createdAt", budget.createdAt)
        }
    }

    private suspend fun buildSavingsGoalsArray(): JSONArray {
        return database.savingsGoalDao().getAll().toJsonArray { goal ->
            JSONObject()
                .put("id", goal.id)
                .put("name", goal.name)
                .put("targetAmount", goal.targetAmount)
                .put("currentAmount", goal.currentAmount)
                .put("targetDate", goal.targetDate)
                .put("createdAt", goal.createdAt)
                .put("updatedAt", goal.updatedAt)
        }
    }

    private suspend fun buildClassificationRulesArray(): JSONArray {
        return database.classificationRuleDao().getAll().toJsonArray { rule ->
            JSONObject()
                .put("id", rule.id)
                .put("pattern", rule.pattern)
                .put("bankEntity", rule.bankEntity)
                .put("categoryId", rule.categoryId)
                .put("priority", rule.priority)
                .put("isActive", rule.isActive)
                .put("createdAt", rule.createdAt)
        }
    }

    private inline fun <T> Iterable<T>.toJsonArray(builder: (T) -> JSONObject): JSONArray {
        return JSONArray().apply {
            for (item in this@toJsonArray) {
                put(builder(item))
            }
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
    }
}
