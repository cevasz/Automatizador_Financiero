package com.finanzas.automatica.domain.model

import java.time.Instant

// Objeto intermedio que produce el parser antes de enriquecer
data class RawMovement(
    val type: MovementType,
    val amount: Long, // En centavos
    val paymentMethod: PaymentMethod,
    val counterpartyRaw: String,
    val date: Instant,
    val bankEntity: BankEntity,
    val rawText: String,
    val confidence: Double = 1.0 // 0.0 - 1.0
)

// Resultado del parseo
sealed interface ParseResult {
    data class Success(val movement: RawMovement) : ParseResult
    data class Failure(val error: String, val rawText: String) : ParseResult
}

// Modelo de dominio para enriquecimiento
data class EnrichedMovement(
    val rawMovement: RawMovement,
    val agendaEntry: AgendaEntry? = null,
    val suggestedCategory: Category? = null,
    val confidence: Double = 0.8,
    val needsConfirmation: Boolean = true
)

enum class AgendaSource {
    MANUAL, COMMUNITY_SUGGESTED, AUTO_LEARNED
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