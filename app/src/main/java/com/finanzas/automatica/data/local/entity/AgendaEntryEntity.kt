package com.finanzas.automatica.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "agenda_entries",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["defaultCategoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["accountIdentifier"], unique = true),
        Index("defaultCategoryId")
    ]
)
data class AgendaEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountIdentifier: String,
    val displayName: String,
    val defaultCategoryId: Long? = null,
    val color: Int = 0xFF8D6E63,
    val origin: String = "MANUAL",
    val createdAt: Long = Instant.now().toEpochMilli(),
    val updatedAt: Long = Instant.now().toEpochMilli()
)