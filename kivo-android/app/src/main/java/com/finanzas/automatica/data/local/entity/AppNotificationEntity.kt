package com.finanzas.automatica.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "app_notifications",
    indices = [Index(value = ["read"]), Index(value = ["createdAt"])]
)
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // MOVEMENTS, IMPORT, BUDGET, GOAL, SYSTEM
    val title: String,
    val message: String,
    val read: Boolean = false,
    val createdAt: Long = Instant.now().toEpochMilli()
) {
    companion object {
        const val TYPE_MOVEMENTS = "MOVEMENTS"
        const val TYPE_IMPORT = "IMPORT"
        const val TYPE_BUDGET = "BUDGET"
        const val TYPE_GOAL = "GOAL"
        const val TYPE_SYSTEM = "SYSTEM"
    }
}
