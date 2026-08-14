package com.finanzas.automatica.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "movements",
    foreignKeys = [
        ForeignKey(
            entity = AgendaEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["counterpartyId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("counterpartyId"),
        Index("categoryId"),
        Index("date"),
        Index("bankEntity"),
        Index("confirmationState"),
        Index(value = ["type", "date"])
    ]
)
data class MovementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "INCOME" or "EXPENSE"
    val amount: Long, // En centavos (COP)
    val paymentMethod: String,
    val counterpartyRaw: String,
    val counterpartyId: Long? = null,
    val categoryId: Long? = null,
    val date: Long, // Unix timestamp en millis
    val source: String, // "NOTIFICATION", "OCR", "MANUAL", "OPEN_FINANCE"
    val confirmationState: String = "PENDING", // "PENDING", "CONFIRMED", "REJECTED", "AUTO_CONFIRMED"
    val bankEntity: String,
    val rawText: String,
    val createdAt: Long = Instant.now().toEpochMilli(),
    val updatedAt: Long = Instant.now().toEpochMilli()
)