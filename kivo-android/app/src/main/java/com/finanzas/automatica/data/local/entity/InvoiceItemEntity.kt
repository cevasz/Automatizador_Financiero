package com.finanzas.automatica.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AgendaEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["debtorContactId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("invoiceId"),
        Index("categoryId"),
        Index("debtorContactId"),
        Index("isDebt"),
        Index("debtStatus")
    ]
)
data class InvoiceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val productName: String,
    val quantity: Int = 1,
    val unitPrice: Long, // Centavos COP
    val totalPrice: Long, // Centavos COP
    val categoryId: Long? = null,
    val isDebt: Boolean = false, // True si este producto se clasificó como deuda de alguien
    val debtorContactId: Long? = null, // ID de la persona en la agenda
    val debtorName: String? = null, // Nombre visible del deudor
    val debtStatus: String = "PENDING", // "PENDING" (por cobrar) o "PAID" (saldado)
    val notes: String? = null,
    val createdAt: Long = Instant.now().toEpochMilli()
)
