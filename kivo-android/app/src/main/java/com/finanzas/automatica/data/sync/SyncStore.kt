package com.finanzas.automatica.data.sync

import android.content.Context

/**
 * Estado persistente de la sincronizacion: sesion y cursor.
 *
 * Sobre donde se guarda el refresh token: `SharedPreferences` en modo privado
 * vive en el almacenamiento interno de la app, al que ninguna otra app puede
 * llegar en un dispositivo sin rootear. No esta cifrado en reposo — igual que
 * la base de datos, que tiene el cifrado pendiente (docs/PENDIENTES.md,
 * "Cifrado en reposo"). Cuando se resuelva eso, esto va en el mismo paquete.
 */
class SyncStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("kivo_sync", Context.MODE_PRIVATE)

    var accessToken: String
        get() = prefs.getString(KEY_ACCESS_TOKEN, "").orEmpty()
        set(v) = prefs.edit().putString(KEY_ACCESS_TOKEN, v).apply()

    var refreshToken: String
        get() = prefs.getString(KEY_REFRESH_TOKEN, "").orEmpty()
        set(v) = prefs.edit().putString(KEY_REFRESH_TOKEN, v).apply()

    var expiresAt: Long
        get() = prefs.getLong(KEY_EXPIRES_AT, 0L)
        set(v) = prefs.edit().putLong(KEY_EXPIRES_AT, v).apply()

    var email: String
        get() = prefs.getString(KEY_EMAIL, "").orEmpty()
        set(v) = prefs.edit().putString(KEY_EMAIL, v).apply()

    var userId: String
        get() = prefs.getString(KEY_USER_ID, "").orEmpty()
        set(v) = prefs.edit().putString(KEY_USER_ID, v).apply()

    /**
     * Marca de tiempo del servidor hasta la que ya se bajaron cambios, en ISO
     * 8601. Vacio significa "nunca se ha sincronizado": el primer pull se lleva
     * todo el historico.
     */
    var pullCursor: String
        get() = prefs.getString(KEY_PULL_CURSOR, "").orEmpty()
        set(v) = prefs.edit().putString(KEY_PULL_CURSOR, v).apply()

    var lastSyncAt: Long
        get() = prefs.getLong(KEY_LAST_SYNC_AT, 0L)
        set(v) = prefs.edit().putLong(KEY_LAST_SYNC_AT, v).apply()

    /**
     * Si se sube o no el texto crudo de la notificacion bancaria.
     *
     * Es el dato mas sensible que maneja Kivo (puede traer nombres de terceros
     * y saldos), asi que el usuario decide. Por defecto **si** se sube, porque
     * sin el la web no puede explicar de donde salio un movimiento ni
     * reclasificarlo; quien prefiera lo contrario lo apaga en Ajustes.
     */
    var uploadRawText: Boolean
        get() = prefs.getBoolean(KEY_UPLOAD_RAW_TEXT, true)
        set(v) = prefs.edit().putBoolean(KEY_UPLOAD_RAW_TEXT, v).apply()

    val isSignedIn: Boolean
        get() = accessToken.isNotBlank() && refreshToken.isNotBlank()

    /** Margen de 60 s: un token que caduca en 5 s no sirve para la peticion. */
    val needsRefresh: Boolean
        get() = System.currentTimeMillis() > expiresAt - 60_000L

    fun save(tokens: AuthTokens) {
        accessToken = tokens.accessToken
        // Un refresh puede responder sin refresh_token nuevo; sobreescribirlo con
        // vacio dejaria la sesion sin forma de renovarse y obligaria a volver a
        // escribir la contraseña cada hora.
        if (tokens.refreshToken.isNotBlank()) refreshToken = tokens.refreshToken
        expiresAt = tokens.expiresAt
        if (tokens.email.isNotBlank()) email = tokens.email
        if (tokens.userId.isNotBlank()) userId = tokens.userId
    }

    /**
     * Cierra sesion y borra todo, **cursor incluido**. Conservar el cursor
     * seria un error sutil: si la proxima sesion fuera de otra cuenta, esa
     * cuenta nunca bajaria nada anterior a este momento y su historial
     * apareceria incompleto sin ninguna señal de por que.
     */
    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_EXPIRES_AT = "expires_at"
        const val KEY_EMAIL = "email"
        const val KEY_USER_ID = "user_id"
        const val KEY_PULL_CURSOR = "pull_cursor"
        const val KEY_LAST_SYNC_AT = "last_sync_at"
        const val KEY_UPLOAD_RAW_TEXT = "upload_raw_text"
    }
}
