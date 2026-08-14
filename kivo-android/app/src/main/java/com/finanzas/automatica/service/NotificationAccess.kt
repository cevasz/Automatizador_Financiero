package com.finanzas.automatica.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService

/**
 * Utilidades para el permiso de "Acceso a notificaciones" que Android exige
 * habilitar manualmente para que [NotificationCaptureService] reciba notificaciones.
 */
object NotificationAccess {

    // La clave de Settings.Secure no tiene constante pública en el SDK
    // (Settings.Secure.ENABLED_NOTIFICATION_LISTENERS no existe en android.jar).
    private const val KEY_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"

    fun isEnabled(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            KEY_ENABLED_NOTIFICATION_LISTENERS
        ) ?: return false
        val className = NotificationCaptureService::class.java.name
        return isListenerEnabled(enabledListeners, context.packageName, className)
    }

    /**
     * Android guarda en Settings.Secure la lista como "paquete/clase.completa"
     * separada por ":". Solo cuenta la forma completa, no prefijos parciales.
     */
    fun isListenerEnabled(enabledListeners: String?, packageName: String, className: String): Boolean {
        if (enabledListeners.isNullOrBlank()) return false
        val target = "$packageName/$className"
        return enabledListeners.split(":").any { it.trim().equals(target, ignoreCase = true) }
    }

    fun openSettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun requestRebind(context: Context) {
        NotificationListenerService.requestRebind(
            ComponentName(context, NotificationCaptureService::class.java)
        )
    }}