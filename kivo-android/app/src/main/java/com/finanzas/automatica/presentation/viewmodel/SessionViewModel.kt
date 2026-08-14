package com.finanzas.automatica.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SessionState(
    val isConnected: Boolean = false,
    val email: String = "",
    val backendUrl: String = "",
    val accessToken: String = "",
    val lastSyncAt: Long? = null
)

class SessionViewModel(
    context: Context
) : ViewModel() {

    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _sessionState = MutableStateFlow(loadSession())
    val sessionState: StateFlow<SessionState> = _sessionState

    fun connect(email: String, backendUrl: String, accessToken: String) {
        val normalizedEmail = email.trim()
        val normalizedUrl = backendUrl.trim().trimEnd('/')
        val normalizedToken = accessToken.trim()

        val newState = SessionState(
            isConnected = true,
            email = normalizedEmail,
            backendUrl = normalizedUrl,
            accessToken = normalizedToken,
            lastSyncAt = _sessionState.value.lastSyncAt
        )

        persist(newState)
        _sessionState.value = newState
    }

    fun disconnect() {
        preferences.edit().clear().apply()
        _sessionState.value = SessionState()
    }

    fun markSynced() {
        val updated = _sessionState.value.copy(lastSyncAt = System.currentTimeMillis())
        persist(updated)
        _sessionState.value = updated
    }

    fun lastSyncLabel(): String {
        val lastSyncAt = _sessionState.value.lastSyncAt ?: return "Sin sincronizar"
        val formatter = SimpleDateFormat("dd MMM HH:mm", Locale("es", "CO"))
        return "Ultima sincronizacion: ${formatter.format(Date(lastSyncAt))}"
    }

    private fun persist(state: SessionState) {
        preferences.edit()
            .putBoolean(KEY_CONNECTED, state.isConnected)
            .putString(KEY_EMAIL, state.email)
            .putString(KEY_BACKEND_URL, state.backendUrl)
            .putString(KEY_ACCESS_TOKEN, state.accessToken)
            .putLong(KEY_LAST_SYNC_AT, state.lastSyncAt ?: -1L)
            .apply()
    }

    private fun loadSession(): SessionState {
        val lastSyncAt = preferences.getLong(KEY_LAST_SYNC_AT, -1L).takeIf { it > 0L }
        return SessionState(
            isConnected = preferences.getBoolean(KEY_CONNECTED, false),
            email = preferences.getString(KEY_EMAIL, "") ?: "",
            backendUrl = preferences.getString(KEY_BACKEND_URL, "") ?: "",
            accessToken = preferences.getString(KEY_ACCESS_TOKEN, "") ?: "",
            lastSyncAt = lastSyncAt
        )
    }

    companion object {
        private const val PREFS_NAME = "finanzas_web_session"
        private const val KEY_CONNECTED = "connected"
        private const val KEY_EMAIL = "email"
        private const val KEY_BACKEND_URL = "backend_url"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_LAST_SYNC_AT = "last_sync_at"
    }
}
