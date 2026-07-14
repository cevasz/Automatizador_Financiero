package com.finanzas.automatica.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: MovementType,
    val iconName: String, // Nombre del icono Material
    val isCustom: Boolean = false,
    val parentCategoryId: Long? = null,
    val createdAt: Instant = Instant.now()
)

enum class MovementType {
    INCOME, EXPENSE
}