package com.finanzas.automatica.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "classification_rules",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("bankEntity"),
        Index("categoryId"),
        Index(value = ["bankEntity", "priority"])
    ]
)
data class ClassificationRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pattern: String,
    val bankEntity: String,
    val categoryId: Long,
    val priority: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = Instant.now().toEpochMilli()
)