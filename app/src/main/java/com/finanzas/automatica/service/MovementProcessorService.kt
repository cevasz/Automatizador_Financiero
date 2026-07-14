package com.finanzas.automatica.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.finanzas.automatica.R
import com.finanzas.automatica.presentation.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovementProcessorService : LifecycleService() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "movement_processor_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Procesar cola de movimientos pendientes
        scope.launch {
            processPendingMovements()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                "Procesador de Movimientos",
                android.app.NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Servicio en segundo plano para procesar movimientos financieros"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Finanzas Automática")
            .setContentText("Procesando movimientos en segundo plano")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private suspend fun processPendingMovements() {
        // TODO: Implementar procesamiento de cola
        // - Obtener movimientos pendientes de enriquecer
        // - Enriquecer con agenda y clasificación
        // - Guardar en base de datos
        // - Notificar a UI si hay cambios
    }
}