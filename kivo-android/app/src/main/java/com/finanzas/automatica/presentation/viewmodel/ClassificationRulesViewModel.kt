package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.data.local.entity.ClassificationRuleEntity
import com.finanzas.automatica.data.sync.Tombstones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Reglas de clasificacion propias.
 *
 * `ClassificationRuleEntity` existia en Room desde el principio y
 * `DefaultClassificationEngine` ya las consultaba en el paso 2 de la
 * clasificacion — pero **ninguna pantalla las exponia**, asi que la tabla
 * siempre estaba vacia y ese paso nunca hacia nada. Esta pantalla es lo que
 * hace util codigo que ya estaba escrito.
 */
class ClassificationRulesViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val ruleDao = database.classificationRuleDao()
    private val categoryDao = database.categoryDao()
    private val tombstones = Tombstones(database)

    private val _rules = MutableStateFlow<List<ClassificationRuleEntity>>(emptyList())
    val rules: StateFlow<List<ClassificationRuleEntity>> = _rules

    val categories: StateFlow<List<CategoryEntity>> = categoryDao.getAllFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { _rules.value = ruleDao.getAll() }
    }

    fun save(
        id: Long?,
        pattern: String,
        bankEntity: String,
        categoryId: Long,
        priority: Int,
        isActive: Boolean
    ) {
        val limpio = pattern.trim()
        // Se valida aqui y no solo en la pantalla porque una expresion invalida
        // no falla al guardarla sino mucho despues, dentro de la clasificacion,
        // donde `runCatching { pattern.toRegex() }` la descarta en silencio: la
        // regla existiria en la lista sin hacer nada nunca.
        val problema = validar(limpio)
        if (problema != null) {
            _error.value = problema
            return
        }

        viewModelScope.launch {
            if (id == null) {
                ruleDao.insert(
                    ClassificationRuleEntity(
                        pattern = limpio,
                        bankEntity = bankEntity,
                        categoryId = categoryId,
                        priority = priority,
                        isActive = isActive
                    )
                )
            } else {
                val actual = ruleDao.getById(id) ?: return@launch
                ruleDao.update(
                    actual.copy(
                        pattern = limpio,
                        bankEntity = bankEntity,
                        categoryId = categoryId,
                        priority = priority,
                        isActive = isActive
                    )
                )
            }
            _error.value = null
            refresh()
        }
    }

    fun setActive(id: Long, activa: Boolean) {
        viewModelScope.launch {
            ruleDao.setActive(id, activa)
            refresh()
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            tombstones.antesDeBorrarRegla(id)
            ruleDao.deleteById(id)
            refresh()
        }
    }

    fun consumeError() {
        _error.value = null
    }

    companion object {
        /** Devuelve el problema del patron, o null si sirve. */
        fun validar(pattern: String): String? {
            if (pattern.isBlank()) return "La regla necesita un texto o expresión a buscar."
            return try {
                pattern.toRegex()
                null
            } catch (e: Exception) {
                "La expresión no es válida: ${e.message?.take(120) ?: "revisa los símbolos"}"
            }
        }

        /**
         * Prueba el patron contra un texto de ejemplo, tal como lo hara el motor
         * de clasificacion (`containsMatchIn` sobre el texto crudo de la
         * notificacion). Devuelve null si el patron no compila.
         */
        fun coincide(pattern: String, textoDePrueba: String): Boolean? =
            runCatching { pattern.toRegex().containsMatchIn(textoDePrueba) }.getOrNull()
    }
}
