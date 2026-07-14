package com.finanzas.automatica.domain.model

import java.time.Instant

enum class MovementType {
    INCOME, EXPENSE
}

enum class PaymentMethod {
    NEQUI, BANCOLOMBIA, DAVIPLATA, NU, LULO, PSE, CASH, CARD, OTHER
}

enum class BankEntity {
    NEQUI, BANCOLOMBIA, DAVIPLATA, NU, LULO, UNKNOWN
}

data class RawMovement(
    val type: MovementType,
    val amount: Long, // En centavos (COP)
    val paymentMethod: PaymentMethod,
    val counterpartyRaw: String, // Número de teléfono, email, o nombre crudo
    val date: Instant,
    val bankEntity: BankEntity,
    val rawText: String,
    val confidence: Double = 0.8 // 0.0 - 1.0
)

sealed interface ParseResult {
    data class Success(val movement: RawMovement) : ParseResult
    data class Failure(val error: String, val rawText: String) : ParseResult
}

data class EnrichedMovement(
    val rawMovement: RawMovement,
    val agendaEntry: AgendaEntry? = null,
    val suggestedCategory: Category? = null,
    val confidence: Double = 0.8
)

data class AgendaEntry(
    val id: Long? = null,
    val identifier: String, // Número de teléfono, email, o cuenta
    val merchantName: String,
    val defaultCategoryId: Long? = null,
    val color: Int = 0xFF8D6E63, // Terracota por defecto
    val source: AgendaSource = AgendaSource.MANUAL,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

enum class AgendaSource {
    MANUAL, COMMUNITY_SUGGESTED, AUTO_LEARNED
}

data class Category(
    val id: Long? = null,
    val name: String,
    val type: MovementType,
    val icon: String, // Material icon name
    val isCustom: Boolean = false,
    val parentId: Long? = null,
    val sortOrder: Int = 0
)

data class Movement(
    val id: Long? = null,
    val type: MovementType,
    val amount: Long,
    val paymentMethod: PaymentMethod,
    val counterpartyRaw: String,
    val counterpartyId: Long? = null, // FK a AgendaEntry
    val categoryId: Long? = null,
    val date: Instant,
    val source: MovementSource,
    val confirmationState: ConfirmationState,
    val bankEntity: BankEntity,
    val rawText: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

enum class MovementSource {
    NOTIFICATION, OCR, MANUAL, OPEN_FINANCE
}

enum class ConfirmationState {
    PENDING, CONFIRMED, REJECTED, AUTO_CONFIRMED
}

data class Budget(
    val id: Long? = null,
    val categoryId: Long,
    val monthlyLimit: Long, // En centavos
    val month: Int, // 1-12
    val year: Int,
    val createdAt: Instant = Instant.now()
)

data class SavingsGoal(
    val id: Long? = null,
    val name: String,
    val targetAmount: Long,
    val currentAmount: Long = 0,
    val targetDate: Instant,
    val createdAt: Instant = Instant.now()
)

data class ClassificationRule(
    val id: Long? = null,
    val pattern: String, // Regex pattern
    val bankEntity: BankEntity,
    val categoryId: Long,
    val priority: Int = 0,
    val isActive: Boolean = true
)