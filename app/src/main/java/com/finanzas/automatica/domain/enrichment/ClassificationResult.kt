package com.finanzas.automatica.domain.enrichment

import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.data.local.entity.ClassificationRuleEntity

data class ClassificationResult(
    val category: CategoryEntity?,
    val confidence: Double,
    val matchedRule: ClassificationRuleEntity? = null,
    val source: ClassificationSource = ClassificationSource.RULES
)

enum class ClassificationSource {
    AGENDA,       // Vino de la agenda financiera
    RULES,        // Matcheó una regla de clasificación
    KEYWORDS,     // Matcheó palabras clave en la contraparte
    HISTORY,      // Basado en histórico del mismo número
    UNKNOWN       // No se pudo clasificar
}