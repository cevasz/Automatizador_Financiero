package com.finanzas.automatica.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Lapida de una fila borrada localmente.
 *
 * Sin esto, un borrado no se puede propagar: la fila simplemente desaparece de
 * Room y en la siguiente sincronizacion no hay nada que decirle al servidor —
 * peor aun, el servidor la devolveria en el pull y "resucitaria" en el
 * telefono. Se guarda solo el [syncId] y la tabla, no el contenido: alcanza
 * para marcarla como borrada arriba, y no deja copias de datos que el usuario
 * quiso eliminar.
 *
 * Las lapidas se borran en cuanto el servidor confirma que recibio el borrado.
 */
@Entity(tableName = "sync_deletions")
data class SyncDeletionEntity(
    @PrimaryKey val syncId: String,
    val tableName: String,
    val deletedAt: Long = Instant.now().toEpochMilli()
)
