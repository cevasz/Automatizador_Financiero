package com.finanzas.automatica.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentCategoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("parentCategoryId")]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // "INCOME" or "EXPENSE"
    val iconName: String,
    val isCustom: Boolean = false,
    val parentCategoryId: Long? = null,
    val sortOrder: Int = 0,
    val createdAt: Long = Instant.now().toEpochMilli()
)