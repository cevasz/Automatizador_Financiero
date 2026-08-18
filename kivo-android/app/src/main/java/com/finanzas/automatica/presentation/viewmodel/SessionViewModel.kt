package com.finanzas.automatica.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.BuildConfig
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.sync.SupabaseClient
import com.finanzas.automatica.data.sync.SupabaseException
import com.finanzas.automatica.data.sync.SyncEngine
import com.finanzas.automatica.data.sync.SyncStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SessionState(
    /** Si esta compilacion trae credenciales de Supabase. Ver local.properties.example. */
    val isConfigured: Boolean = false,
    val isConnected: Boolean = false,
    val email: String = "",
    val lastSyncAt: Long? = null,
    val syncing: Boolean = false,
    val uploadRawText: Boolean = true,
    /** Mensaje de exito para mostrar una vez (se limpia al leerlo). */
    val message: String? = null,
    val error: String? = null
)

/**
 * Cuenta de Kivo y sincronizacion con la nube.
 *
 * Antes esta pantalla pedia "URL del backend" y "token de acceso" a mano y el
 * boton "Sincronizar" solo escribia un JSON en el almacenamiento interno del
 * telefono — no salia nada a ninguna parte. Ahora es una sesion real contra
 * Supabase (ver backend/README.md) y la sincronizacion sube y baja de verdad.
 */
class SessionViewModel(
    context: Context,
    database: FinanzasDatabase
) : ViewModel() {

    private val store = SyncStore(context)
    private val client = SupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY)
    private val engine = SyncEngine(database, client, store)

    private val _sessionState = MutableStateFlow(leerEstado())
    val sessionState: StateFlow<SessionState> = _sessionState

    fun signIn(email: String, password: String) = enSesion("No se pudo entrar") {
        engine.signIn(email, password)
        "Sesión iniciada. Toca Sincronizar para subir tus datos."
    }

    fun signUp(email: String, password: String) = enSesion("No se pudo crear la cuenta") {
        engine.signUp(email, password)
        "Cuenta creada. Toca Sincronizar para subir tus datos."
    }

    fun signOut() {
        engine.signOut()
        _sessionState.value = leerEstado().copy(message = "Sesión cerrada. Tus datos locales siguen intactos.")
    }

    fun syncNow() = enSesion("No se pudo sincronizar") {
        val resultado = engine.sync()
        "Sincronizado: ${resultado.bajados} cambios recibidos, ${resultado.subidos} registros enviados" +
            if (resultado.borradosPropagados > 0) ", ${resultado.borradosPropagados} borrados propagados." else "."
    }

    fun setUploadRawText(activo: Boolean) {
        store.uploadRawText = activo
        _sessionState.value = _sessionState.value.copy(uploadRawText = activo)
    }

    /** Limpia el mensaje/error ya mostrado para que no reaparezca al rotar la pantalla. */
    fun consumeMessage() {
        _sessionState.value = _sessionState.value.copy(message = null, error = null)
    }

    fun lastSyncLabel(): String {
        val cuando = _sessionState.value.lastSyncAt ?: return "Sin sincronizar"
        val formato = SimpleDateFormat("dd MMM HH:mm", Locale("es", "CO"))
        return "Última sincronización: ${formato.format(Date(cuando))}"
    }

    /**
     * Envoltura comun: marca "sincronizando", ejecuta en IO y traduce cualquier
     * fallo a un mensaje para el usuario. Atrapa `Throwable` — un fallo de TLS
     * o de la libreria de red puede llegar como Error y, en una corrutina sin
     * manejador, tumbaria el proceso entero (el mismo patron que ya se corrigio
     * al leer PDFs y al procesar notificaciones).
     */
    private fun enSesion(prefijoError: String, bloque: suspend () -> String) {
        _sessionState.value = _sessionState.value.copy(syncing = true, message = null, error = null)
        viewModelScope.launch(Dispatchers.IO) {
            val estado = try {
                val mensaje = bloque()
                leerEstado().copy(message = mensaje)
            } catch (e: SupabaseException) {
                Log.w(TAG, prefijoError, e)
                leerEstado().copy(error = e.message ?: prefijoError)
            } catch (e: Throwable) {
                Log.e(TAG, prefijoError, e)
                leerEstado().copy(error = "$prefijoError: ${e.message ?: e::class.java.simpleName}")
            }
            _sessionState.value = estado.copy(syncing = false)
        }
    }

    private fun leerEstado() = SessionState(
        isConfigured = client.isConfigured,
        isConnected = store.isSignedIn,
        email = store.email,
        lastSyncAt = store.lastSyncAt.takeIf { it > 0L },
        uploadRawText = store.uploadRawText
    )

    private companion object {
        const val TAG = "SessionViewModel"
    }
}
