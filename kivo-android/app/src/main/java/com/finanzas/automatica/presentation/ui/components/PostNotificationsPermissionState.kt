package com.finanzas.automatica.presentation.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * Estado real del permiso "Enviar notificaciones" (POST_NOTIFICATIONS, Android 13+).
 *
 * Antes la app declaraba este permiso en el manifiesto pero NUNCA lo pedia en tiempo de
 * ejecucion -- en Android 13+ eso significa que quedaba denegado por defecto y ninguna
 * notificacion local (meta de ahorro lograda, resumen de importacion, etc.) se mostraba
 * nunca, en silencio, sin que el usuario supiera que la app ni siquiera habia pedido
 * permiso. Se re-verifica al volver (ON_RESUME) porque el usuario puede cambiarlo desde
 * los Ajustes de Android en cualquier momento.
 */
private fun isPostNotificationsGranted(context: android.content.Context): Boolean {
    // Antes de Android 13 (API 33) este permiso no existe -- las notificaciones locales
    // siempre estan permitidas por defecto, no hay nada que pedir.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun rememberPostNotificationsGranted(): Boolean {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var granted by remember { mutableStateOf(isPostNotificationsGranted(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                granted = isPostNotificationsGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    return granted
}
