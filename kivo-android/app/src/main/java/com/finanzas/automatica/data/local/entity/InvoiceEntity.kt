package com.finanzas.automatica.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = MovementEntity::class,
            parentColumns = ["id"],
            childColumns = ["movementId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("movementId"),
        Index("date")
    ]
)
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val merchantName: String,
    val date: Long = Instant.now().toEpochMilli(),
    val totalAmount: Long, // En centavos COP
    val imageUri: String? = null,
    val movementId: Long? = null,
    val createdAt: Long = Instant.now().toEpochMilli()
)
