package com.finanzas.automatica.domain.enrichment

import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.dao.AgendaDao
import com.finanzas.automatica.data.local.dao.CategoryDao
import com.finanzas.automatica.data.local.dao.ClassificationRuleDao
import com.finanzas.automatica.data.local.entity.AgendaEntryEntity
import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.data.local.entity.ClassificationRuleEntity
import com.finanzas.automatica.data.local.entity.MovementEntity
import com.finanzas.automatica.data.repository.MovementRepositoryImpl
import com.finanzas.automatica.domain.model.ConfirmationState
import com.finanzas.automatica.domain.model.MovementSource
import com.finanzas.automatica.domain.parser.ParseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EnrichmentPipeline(
    private val database: FinanzasDatabase
) {

    private val movementRepository = MovementRepositoryImpl(database.movementDao())
    private val agendaDao = database.agendaDao()
    private val categoryDao = database.categoryDao()
    private val ruleDao = database.classificationRuleDao()

    suspend fun process(rawMovement: RawMovement) {
        withContext(Dispatchers.IO) {
            // 1. Buscar en agenda
            val agendaEntry = agendaDao.getByIdentifier(rawMovement.counterpartyRaw)
            
            // 2. Buscar regla de clasificación
            val suggestedCategory = findSuggestedCategory(rawMovement, agendaEntry)
            
            // 3. Determinar si necesita confirmación
            val needsConfirmation = determineNeedsConfirmation(rawMovement, agendaEntry, suggestedCategory)
            
            // 4. Crear movimiento enriquecido
            val enriched = EnrichedMovement(
                rawMovement = rawMovement,
                agendaEntry = agendaEntry?.toDomain(),
                suggestedCategory = suggestedCategory?.toDomain(),
                confidence = calculateConfidence(rawMovement, agendaEntry, suggestedCategory),
                needsConfirmation = needsConfirmation
            )

            // 5. Guardar
            saveEnriched(enriched)
        }
    }

    private suspend fun findSuggestedCategory(
        rawMovement: RawMovement,
        agendaEntry: AgendaEntryEntity?
    ): CategoryEntity? {
        // Primero: si hay agenda con categoría por defecto
        agendaEntry?.defaultCategoryId?.let { categoryId ->
            categoryDao.getById(categoryId)?.also { return it }
        }

        // Segundo: buscar reglas de clasificación para este banco
        val rules = ruleDao.getActiveByBank(rawMovement.bankEntity.name)
        for (rule in rules) {
            if (rule.pattern.toRegex().matches(rawMovement.rawText)) {
                val category = categoryDao.getById(rule.categoryId)
                if (category != null) return category
            }
        }

        // Tercero: buscar por palabras clave en categorías existentes
        val categories = categoryDao.getByType(rawMovement.type.name)
        for (category in categories) {
            // Podríamos tener palabras clave en el nombre o icono
            if (rawMovement.rawText.lowercase().contains(category.name.lowercase())) {
                return category
            }
        }

        return null
    }

    private fun determineNeedsConfirmation(
        rawMovement: RawMovement,
        agendaEntry: AgendaEntryEntity?,
        suggestedCategory: CategoryEntity?
    ): Boolean {
        // Si hay agenda y categoría sugerida, confianza alta
        if (agendaEntry != null && suggestedCategory != null) return false
        
        // Si la confianza del parser es muy alta (>0.95) y hay categoría sugerida
        if (rawMovement.confidence > 0.95 && suggestedCategory != null) return false

        // Si es un comercio conocido en la agenda
        if (agendaEntry != null) return false

        return true
    }

    private fun calculateConfidence(
        rawMovement: RawMovement,
        agendaEntry: AgendaEntryEntity?,
        suggestedCategory: CategoryEntity?
    ): Double {
        var confidence = rawMovement.confidence
        
        if (agendaEntry != null) confidence = minOf(1.0, confidence + 0.15)
        if (suggestedCategory != null) confidence = minOf(1.0, confidence + 0.1)
        
        return confidence
    }

    private suspend fun saveEnriched(enriched: EnrichedMovement) {
        val movement = MovementEntity(
            type = enriched.rawMovement.type.name,
            amount = enriched.rawMovement.amount,
            paymentMethod = enriched.rawMovement.paymentMethod.name,
            counterpartyRaw = enriched.rawMovement.counterpartyRaw,
            counterpartyId = enriched.agendaEntry?.id,
            categoryId = enriched.suggestedCategory?.id,
            date = enriched.rawMovement.date.toEpochMilli(),
            source = MovementSource.NOTIFICATION.name,
            confirmationState = if (enriched.needsConfirmation) 
                ConfirmationState.PENDING.name 
            else 
                ConfirmationState.AUTO_CONFIRMED.name,
            bankEntity = enriched.rawMovement.bankEntity.name,
            rawNotificationText = enriched.rawMovement.rawText
        )

        movementRepository.insert(movement)
    }
}

// Modelo de dominio para enriquecimiento
data class EnrichedMovement(
    val rawMovement: RawMovement,
    val agendaEntry: AgendaEntry? = null,
    val suggestedCategory: Category? = null,
    val confidence: Double = 0.8,
    val needsConfirmation: Boolean = true
)

data class AgendaEntry(
    val id: Long,
    val accountIdentifier: String,
    val displayName: String,
    val defaultCategoryId: Long?,
    val color: Int,
    val origin: String,
    val createdAt: Long,
    val updatedAt: Long
)

data class Category(
    val id: Long,
    val name: String,
    val type: String,
    val iconName: String,
    val isCustom: Boolean,
    val parentCategoryId: Long?,
    val sortOrder: Int,
    val createdAt: Long
)

fun AgendaEntryEntity.toDomain(): AgendaEntry = AgendaEntry(
    id = id,
    accountIdentifier = accountIdentifier,
    displayName = displayName,
    defaultCategoryId = defaultCategoryId,
    color = color,
    origin = origin,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    type = type,
    iconName = iconName,
    isCustom = isCustom,
    parentCategoryId = parentCategoryId,
    sortOrder = sortOrder,
    createdAt = createdAt
)