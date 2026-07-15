package com.finanzas.automatica.domain.enrichment

import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.data.local.entity.ClassificationRuleEntity
import com.finanzas.automatica.domain.model.RawMovement
import kotlinx.coroutines.flow.Flow

interface ClassificationEngine {
    suspend fun classify(movement: RawMovement): ClassificationResult
}

class DefaultClassificationEngine(
    private val agendaRepository: AgendaRepository,
    private val ruleRepository: ClassificationRuleRepository,
    private val keywordRepository: KeywordRepository
) : ClassificationEngine {

    override suspend fun classify(movement: RawMovement): ClassificationResult {
        // 1. Intentar buscar en agenda por identificador exacto
        val agendaEntry = agendaRepository.findByIdentifier(movement.counterpartyRaw)
        if (agendaEntry != null && agendaEntry.defaultCategoryId != null) {
            val category = getCategoryById(agendaEntry.defaultCategoryId!!)
            if (category != null) {
                return ClassificationResult(
                    category = category,
                    confidence = 0.95,
                    source = ClassificationSource.AGENDA
                )
            }
        }

        // 2. Intentar reglas de clasificación por banco
        val rules = ruleRepository.getActiveRulesForBank(movement.bankEntity.name)
        for (rule in rules) {
            if (movement.rawText.matches(rule.pattern.toRegex())) {
                val category = getCategoryById(rule.categoryId)
                if (category != null) {
                    return ClassificationResult(
                        category = category,
                        confidence = 0.85,
                        matchedRule = rule,
                        source = ClassificationSource.RULES
                    )
                }
            }
        }

        // 3. Buscar palabras clave en la contraparte
        val keywordMatch = keywordRepository.findMatchingKeyword(movement.counterpartyRaw)
        if (keywordMatch != null) {
            val category = getCategoryById(keywordMatch.categoryId)
            if (category != null) {
                return ClassificationResult(
                    category = category,
                    confidence = 0.75,
                    source = ClassificationSource.KEYWORDS
                )
            }
        }

        // 4. Buscar en histórico por mismo número/identificador
        val historicalCategory = findHistoricalCategory(movement.counterpartyRaw)
        if (historicalCategory != null) {
            return ClassificationResult(
                category = historicalCategory,
                confidence = 0.70,
                source = ClassificationSource.HISTORY
            )
        }

        // 5. No se pudo clasificar
        return ClassificationResult(
            category = null,
            confidence = 0.0,
            source = ClassificationSource.UNKNOWN
        )
    }

    private suspend fun getCategoryById(id: Long): CategoryEntity? {
        // TODO: Implementar lookup de categoría
        return null
    }

    private suspend fun findHistoricalCategory(counterparty: String): CategoryEntity? {
        // TODO: Buscar en movimientos históricos del mismo contraparte
        return null
    }
}

interface ClassificationRuleRepository {
    suspend fun getActiveRulesForBank(bankEntity: String): List<ClassificationRuleEntity>
    fun observeActiveRulesForBank(bankEntity: String): Flow<List<ClassificationRuleEntity>>
}

interface KeywordRepository {
    suspend fun findMatchingKeyword(counterparty: String): KeywordMatch?
    data class KeywordMatch(val keyword: String, val categoryId: Long)
}