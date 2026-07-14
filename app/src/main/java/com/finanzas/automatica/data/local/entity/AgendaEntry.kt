package com.finanzas.automatica.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.finanzas.automatica.data.local.converters.Converters
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
@TypeConverters(Converters::class)
data class AgendaEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountIdentifier: String, // Número de teléfono, email, o número de cuenta
    val displayName: String, // Nombre del comercio/persona
    val defaultCategoryId: Long? = null,
    val color: Int = 0xFF8D6E63, // Terracota por defecto
    val origin: AgendaOrigin = AgendaOrigin.MANUAL,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

enum class AgendaOrigin {
    MANUAL, COMMUNITY_SUGGESTED, AUTO_DETECTED
}