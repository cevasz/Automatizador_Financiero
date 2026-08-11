package com.finanzas.automatica.domain.model

import java.time.Instant

data class Movement(
    val id: Long = 0,
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
    PENDING, CONFIRMED, REJECTED, AUTO_CONFIRMED
}

enum class BankEntity {
    NEQUI, BANCOLOMBIA, DAVIPLATA, NU, LULO, UNKNOWN
}