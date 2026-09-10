package com.finanzas.automatica.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Registro local de qué notificaciones bancarias llegaron y qué se hizo con ellas.
 *
 * Existe para responder, desde el teléfono, la pregunta que hasta ahora solo se podía
 * contestar con el celular conectado a logcat: "llegó la notificación y no quedó el
 * movimiento, ¿por qué?". Guarda el texto tal como se recibió, para poder copiarlo y
 * convertirlo en un caso de prueba real (las reglas del proyecto prohíben inventar el
 * formato de una notificación, ver CLAUDE.md).
 *
 * Privacidad: esta tabla es **solo local**. No tiene `syncId` ni entra en la
 * sincronización con Supabase, se poda sola a las últimas [MAX_ENTRIES] entradas y el
 * usuario puede borrarla entera desde la pantalla de diagnóstico. De las apps de
 * mensajería solo se escribe lo que se reconoció como aviso del banco: un mensaje
 * personal que mencione a Nequi se descarta antes de llegar aquí (ver `CaptureRejection`).
 */
@Entity(
    tableName = "capture_log",
    indices = [Index(value = ["createdAt"])]
)
data class CaptureLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Paquete de la app que emitió la notificación. */
    val packageName: String,
    /** Banco reconocido, o `null` si ninguno lo reclamó. */
    val bankEntity: String?,
    /** CAPTURED, IGNORED o FAILED. */
    val outcome: String,
    /** Nombre del `CaptureRejection`, o `null` cuando se capturó. */
    val reasonCode: String?,
    /** Explicación en español, ya lista para mostrar. */
    val reason: String,
    /** Texto completo de la notificación, tal como llegó. */
    val rawText: String,
    /** Resumen del movimiento cuando sí se capturó ("Egreso $15.500 · LA 14"). */
    val movementSummary: String?,
    val createdAt: Long = Instant.now().toEpochMilli()
) {
    companion object {
        const val OUTCOME_CAPTURED = "CAPTURED"
        const val OUTCOME_IGNORED = "IGNORED"
        const val OUTCOME_FAILED = "FAILED"

        /** Tope de filas conservadas. Es un diagnóstico, no un historial. */
        const val MAX_ENTRIES = 200
    }
}
