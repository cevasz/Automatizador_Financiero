package com.finanzas.automatica.domain.enrichment

import com.finanzas.automatica.data.local.dao.CategoryDao
import com.finanzas.automatica.data.local.dao.ClassificationRuleDao
import com.finanzas.automatica.data.local.dao.MovementDao
import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.data.local.entity.ClassificationRuleEntity
import com.finanzas.automatica.data.local.entity.MovementEntity
import com.finanzas.automatica.domain.model.RawMovement
import kotlinx.coroutines.flow.Flow
import java.text.Normalizer

interface ClassificationEngine {
    suspend fun classify(movement: RawMovement): ClassificationResult
}

interface CategoryLookupRepository {
    suspend fun getById(id: Long): CategoryEntity?
}

interface MovementHistoryRepository {
    suspend fun findByCounterpartyRaw(counterpartyRaw: String): List<MovementEntity>
}

object ClassificationRepositoryProvider {
    @Volatile
    var categoryLookupRepository: CategoryLookupRepository? = null

    @Volatile
    var movementHistoryRepository: MovementHistoryRepository? = null
}

class DefaultClassificationEngine(
    private val agendaRepository: AgendaRepository,
    private val ruleRepository: ClassificationRuleRepository,
    private val keywordRepository: KeywordRepository,
    private val categoryRepository: CategoryLookupRepository? = ClassificationRepositoryProvider.categoryLookupRepository,
    private val movementHistoryRepository: MovementHistoryRepository? = ClassificationRepositoryProvider.movementHistoryRepository
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
            val regex = runCatching { rule.pattern.toRegex() }.getOrNull() ?: continue
            if (regex.containsMatchIn(movement.rawText)) {
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
        return categoryRepository?.getById(id)
            ?: ClassificationRepositoryProvider.categoryLookupRepository?.getById(id)
    }

    private suspend fun findHistoricalCategory(counterparty: String): CategoryEntity? {
        val repository = movementHistoryRepository
            ?: ClassificationRepositoryProvider.movementHistoryRepository
            ?: return null

        val normalizedCounterparty = normalizeText(counterparty)
        if (normalizedCounterparty.isBlank()) return null

        val historicalMovements = repository.findByCounterpartyRaw(counterparty)
            .filter { movement ->
                movement.categoryId != null &&
                    normalizeText(movement.counterpartyRaw) == normalizedCounterparty
            }

        if (historicalMovements.isEmpty()) return null

        val bestCategoryId = historicalMovements
            .groupBy { it.categoryId!! }
            .entries
            .maxWithOrNull(
                compareBy<Map.Entry<Long, List<MovementEntity>>> { it.value.size }
                    .thenByDescending { entry ->
                        entry.value.maxOfOrNull { it.updatedAt } ?: 0L
                    }
            )
            ?.key

        return bestCategoryId?.let { getCategoryById(it) }
    }

    private fun normalizeText(text: String): String {
        return Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .lowercase()
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

class RoomClassificationRuleRepository(
    private val ruleDao: ClassificationRuleDao
) : ClassificationRuleRepository {
    override suspend fun getActiveRulesForBank(bankEntity: String): List<ClassificationRuleEntity> {
        return ruleDao.getActiveByBank(bankEntity)
    }

    override fun observeActiveRulesForBank(bankEntity: String): Flow<List<ClassificationRuleEntity>> {
        return ruleDao.getActiveByBankFlow(bankEntity)
    }
}

class RoomCategoryLookupRepository(
    private val categoryDao: CategoryDao
) : CategoryLookupRepository {
    override suspend fun getById(id: Long): CategoryEntity? {
        return categoryDao.getById(id)
    }
}

class RoomMovementHistoryRepository(
    private val movementDao: MovementDao
) : MovementHistoryRepository {
    override suspend fun findByCounterpartyRaw(counterpartyRaw: String): List<MovementEntity> {
        return movementDao.getByCounterpartyRaw(counterpartyRaw)
    }
}

class DefaultKeywordRepository(
    private val categoryDao: CategoryDao
) : KeywordRepository {

    private val keywordMap = linkedMapOf(
        "supermercado" to listOf("exito", "carulla", "d1", "ara", "olimpica", "jumbo", "metro"),
        "restaurantes" to listOf("restaurante", "restaurant", "mcdonald", "burger", "kfc", "pizza", "rappi"),
        "comida rapida" to listOf("burger king", "dominos", "subway", "papa johns"),
        "cafe/desayuno" to listOf("juan valdez", "oma", "starbucks", "cafe"),
        "taxi/uber/didi" to listOf("uber", "didi", "cabify", "taxi"),
        "servicios publicos" to listOf("energia", "electricidad", "acueducto", "gas", "agua", "enel", "codensa"),
        "internet/telefonia" to listOf("claro", "movistar", "tigo", "wom", "etb", "internet", "telefonia"),
        "farmacia" to listOf("farmacia", "drogueria", "cruz verde", "farmatodo"),
        "salario" to listOf("nomina", "nomina", "salary", "salario"),
        "freelance/independiente" to listOf("freelance", "independiente", "honorarios", "servicios profesionales"),
        "ventas" to listOf("venta", "ventas", "marketplace", "mercado libre"),
        "regalos/donaciones" to listOf("regalo", "donacion", "donación"),
        "otros gastos" to listOf("pago", "transferencia", "consignacion", "consignación")
    )

    override suspend fun findMatchingKeyword(counterparty: String): KeywordRepository.KeywordMatch? {
        val normalizedCounterparty = normalizeText(counterparty)
        if (normalizedCounterparty.isBlank()) return null

        val categoriesByName = categoryDao.getAll().associateBy { normalizeText(it.name) }

        for ((categoryName, keywords) in keywordMap) {
            if (keywords.any { keyword -> normalizedCounterparty.contains(normalizeText(keyword)) }) {
                val category = categoriesByName[normalizeText(categoryName)]
                    ?: categoriesByName.values.firstOrNull { normalizeText(it.name).contains(normalizeText(categoryName)) }
                if (category != null) {
                    return KeywordRepository.KeywordMatch(keywords.first(), category.id)
                }
            }
        }

        val directMatch = categoriesByName.values.firstOrNull { category ->
            normalizedCounterparty.contains(normalizeText(category.name))
        }

        return directMatch?.let {
            KeywordRepository.KeywordMatch(it.name, it.id)
        }
    }

    private fun normalizeText(text: String): String {
        return Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .lowercase()
    }
}
