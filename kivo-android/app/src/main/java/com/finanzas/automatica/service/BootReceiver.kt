package com.finanzas.automatica.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BootReceiver", "Boot completado - re-enlazando listener de notificaciones")
            // Un NotificationListenerService no se inicia con startService: el sistema
            // debe re-enlazarlo si el usuario mantiene habilitado el acceso a notificaciones.
            NotificationAccess.requestRebind(context)
        }
    }
}