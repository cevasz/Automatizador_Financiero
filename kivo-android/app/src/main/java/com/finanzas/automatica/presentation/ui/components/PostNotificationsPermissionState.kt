package com.finanzas.automatica.presentation.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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

private const val PREFS_NAME = "finanzas_settings"
private const val KEY_ASKED_POST_NOTIFICATIONS = "asked_post_notifications"

private fun hasAskedPostNotificationsBefore(context: Context): Boolean =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(KEY_ASKED_POST_NOTIFICATIONS, false)

private fun markPostNotificationsAsked(context: Context) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(KEY_ASKED_POST_NOTIFICATIONS, true)
        .apply()
}

/**
 * Pide POST_NOTIFICATIONS de forma gradual y contextual, no al abrir la app por primera
 * vez: justo cuando [notificationCaptureActive] se vuelve cierto (el usuario acaba de
 * habilitar la captura de notificaciones bancarias, asi que en cualquier momento va a
 * llegar una notificacion local de "movimientos capturados" o una meta lograda). Se pide
 * como maximo UNA vez en la vida de la instalacion -- se acepte o se rechace, no se
 * vuelve a insistir automaticamente; el usuario siempre puede activarlo despues a mano
 * desde Ajustes (ver [rememberPostNotificationsGranted] + la fila en SettingsScreen).
 */
@Composable
fun AutoRequestPostNotificationsWhenRelevant(notificationCaptureActive: Boolean) {
    val context = LocalContext.current
    val granted = rememberPostNotificationsGranted()
    val needsRuntimePermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* rememberPostNotificationsGranted() se re-verifica solo en el siguiente ON_RESUME */ }

    LaunchedEffect(notificationCaptureActive, granted) {
        if (needsRuntimePermission &&
            notificationCaptureActive &&
            !granted &&
            !hasAskedPostNotificationsBefore(context)
        ) {
            markPostNotificationsAsked(context)
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
