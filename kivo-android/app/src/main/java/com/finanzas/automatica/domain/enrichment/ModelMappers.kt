package com.finanzas.automatica.domain.enrichment

import android.util.Log
import com.finanzas.automatica.data.local.entity.AgendaEntryEntity
import com.finanzas.automatica.data.local.entity.BudgetEntity
import com.finanzas.automatica.data.local.entity.MovementEntity
import com.finanzas.automatica.data.local.entity.SavingsGoalEntity
import com.finanzas.automatica.domain.model.AgendaEntry
import com.finanzas.automatica.domain.model.AgendaOrigin
import com.finanzas.automatica.domain.model.Budget
import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.ConfirmationState
import com.finanzas.automatica.domain.model.Movement
import com.finanzas.automatica.domain.model.MovementSource
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.SavingsGoal
import java.time.Instant

/**
 * Convierte una lista de entidades de Room a modelo de dominio, descartando en silencio
 * (con un log) cualquier fila que no se pueda mapear -- por ejemplo, si un campo tipo
 * enum quedo con un valor que no coincide con ningun `MovementType`/`BankEntity`/etc.
 * (dato corrupto, version anterior con un enum distinto, etc.).
 *
 * Sin esto, UNA sola fila mal formada tumbaba la lista COMPLETA -- y con ella cualquier
 * pantalla que la mostrara, incluido el Dashboard justo al abrir la app, porque
 * `.toDomain()` corre dentro del `Flow` reactivo que alimenta cada `StateFlow` de los
 * ViewModel (`categoryRepository.getAllFlow().map { it.toDomain() }.stateIn(...)`), sin
 * ningun try/catch alrededor -- eso convertia un solo registro problemático en un
 * crash en cada apertura de la app (bug reportado: "sigue sin abrir cuando entro").
 */
inline fun <E, D> List<E>.toDomainSafely(crossinline mapper: (E) -> D): List<D> =
    mapNotNull { entity ->
        try {
            mapper(entity)
        } catch (t: Throwable) {
            Log.e("ModelMappers", "No se pudo mapear una fila a modelo de dominio, se omite: $entity", t)
            null
        }
    }

fun AgendaEntry.toEntity(): AgendaEntryEntity = AgendaEntryEntity(
    id = id,
    accountIdentifier = accountIdentifier,
    displayName = displayName,
    defaultCategoryId = defaultCategoryId,
    color = color,
    origin = origin.name,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

fun AgendaEntryEntity.toDomain(): AgendaEntry = AgendaEntry(
    id = id,
    accountIdentifier = accountIdentifier,
    displayName = displayName,
    defaultCategoryId = defaultCategoryId,
    color = color,
    origin = AgendaOrigin.valueOf(origin),
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt)
)

fun Budget.toEntity(): BudgetEntity = BudgetEntity(
    id = id ?: 0,
    categoryId = categoryId,
    monthlyLimit = monthlyLimit,
    month = month,
    year = year,
    createdAt = createdAt.toEpochMilli()
)

fun BudgetEntity.toDomain(): Budget = Budget(
    id = id,
    categoryId = categoryId,
    monthlyLimit = monthlyLimit,
    month = month,
    year = year,
    createdAt = Instant.ofEpochMilli(createdAt)
)

fun SavingsGoal.toEntity(): SavingsGoalEntity = SavingsGoalEntity(
    id = id ?: 0,
    name = name,
    targetAmount = targetAmount,
    currentAmount = currentAmount,
    targetDate = targetDate.toEpochMilli(),
    createdAt = createdAt.toEpochMilli(),
    updatedAt = Instant.now().toEpochMilli()
)

fun SavingsGoalEntity.toDomain(): SavingsGoal = SavingsGoal(
    id = id,
    name = name,
    targetAmount = targetAmount,
    currentAmount = currentAmount,
    targetDate = Instant.ofEpochMilli(targetDate),
    createdAt = Instant.ofEpochMilli(createdAt)
)

fun MovementEntity.toDomain(): Movement = Movement(
    id = id,
    type = MovementType.valueOf(type),
    amount = amount,
    paymentMethod = PaymentMethod.valueOf(paymentMethod),
    counterpartyRaw = counterpartyRaw,
    counterpartyId = counterpartyId,
    categoryId = categoryId,
    date = Instant.ofEpochMilli(date),
    source = MovementSource.valueOf(source),
    confirmationState = ConfirmationState.valueOf(confirmationState),
    bankEntity = BankEntity.valueOf(bankEntity),
    rawNotificationText = rawText,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt)
)

fun Movement.toEntity(): MovementEntity = MovementEntity(
    id = id,
    type = type.name,
    amount = amount,
    paymentMethod = paymentMethod.name,
    counterpartyRaw = counterpartyRaw,
    counterpartyId = counterpartyId,
    categoryId = categoryId,
    date = date.toEpochMilli(),
    source = source.name,
    confirmationState = confirmationState.name,
    bankEntity = bankEntity.name,
    rawText = rawNotificationText,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)
