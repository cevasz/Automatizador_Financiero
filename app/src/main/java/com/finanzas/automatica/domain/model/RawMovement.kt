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