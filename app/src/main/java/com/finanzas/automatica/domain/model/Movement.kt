package com.finanzas.automatica.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.finanzas.automatica.data.local.converters.Converters
import java.time.Instant

@Entity(tableName = "movements")
@TypeConverters(Converters::class)
data class Movement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: MovementType,
    val amount: Long, // En centavos (COP)
    val paymentMethod: PaymentMethod,
    val counterpartyRaw: String, // Texto crudo de la contraparte (número, nombre, etc.)
    val counterpartyId: Long? = null, // FK a AgendaEntry
    val categoryId: Long? = null, // FK a Category
    val date: Instant,
    val source: MovementSource,
    val confirmationState: ConfirmationState = ConfirmationState.PENDING,
    val bankEntity: BankEntity,
    val rawNotificationText: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

enum class MovementType {
    INCOME, EXPENSE
}

enum class PaymentMethod {
    NEQUI, BANCOLOMBIA, DAVIPLATA, NU, LULO, PSE, QR, CASH, OTHER
}

enum class MovementSource {
    NOTIFICATION, OCR, MANUAL, OPEN_FINANCE
}

enum class ConfirmationState {
    PENDING, CONFIRMED, CORRECTED, REJECTED
}

enum class BankEntity {
    NEQUI, BANCOLOMBIA, DAVIPLATA, NU, LULO, UNKNOWN
}