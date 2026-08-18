package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.data.sync.Tombstones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Cuanto se perderia al borrar una categoria. Se muestra ANTES de confirmar,
 * porque el borrado no es inocuo: por las llaves foraneas, los presupuestos y
 * las reglas de esa categoria se borran en cascada, y los movimientos que la
 * usaban se quedan sin clasificar (SET NULL). Sin este aviso, alguien borraria
 * "Supermercado" y perderia su presupuesto de mercado sin enterarse.
 */
data class CategoryImpact(
    val movements: Int = 0,
    val budgets: Int = 0,
    val rules: Int = 0,
    val agendaEntries: Int = 0
) {
    val isEmpty: Boolean get() = movements == 0 && budgets == 0 && rules == 0 && agendaEntries == 0
}

/**
 * Administracion de categorias propias.
 *
 * Hasta ahora las 33 categorias sembradas por [com.finanzas.automatica.data.repository.DefaultCategories]
 * eran las unicas posibles: no habia forma de crear una, renombrarla ni
 * eliminarla desde la app, aunque el modelo ya tenia el campo `isCustom`.
 */
class CategoriesViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val categoryDao = database.categoryDao()
    private val tombstones = Tombstones(database)

    val categories: StateFlow<List<CategoryEntity>> = categoryDao.getAllFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _impacts = MutableStateFlow<Map<Long, CategoryImpact>>(emptyMap())
    val impacts: StateFlow<Map<Long, CategoryImpact>> = _impacts

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        refreshImpacts()
    }

    fun refreshImpacts() {
        viewModelScope.launch {
            val acumulado = mutableMapOf<Long, CategoryImpact>()

            categoryDao.movementUsage().forEach {
                acumulado[it.categoryId] = (acumulado[it.categoryId] ?: CategoryImpact())
                    .copy(movements = it.total)
            }
            categoryDao.budgetUsage().forEach {
                acumulado[it.categoryId] = (acumulado[it.categoryId] ?: CategoryImpact())
                    .copy(budgets = it.total)
            }
            categoryDao.ruleUsage().forEach {
                acumulado[it.categoryId] = (acumulado[it.categoryId] ?: CategoryImpact())
                    .copy(rules = it.total)
            }
            categoryDao.agendaUsage().forEach {
                acumulado[it.categoryId] = (acumulado[it.categoryId] ?: CategoryImpact())
                    .copy(agendaEntries = it.total)
            }

            _impacts.value = acumulado
        }
    }

    fun create(name: String, type: String, iconName: String) {
        val limpio = name.trim()
        if (limpio.isBlank()) {
            _error.value = "La categoría necesita un nombre."
            return
        }

        viewModelScope.launch {
            // Se compara sin distinguir mayusculas: "Mercado" y "mercado" son la
            // misma categoria para quien la escribe, y tenerlas por separado
            // parte los totales en dos sin que se note por que.
            val yaExiste = categories.value.any {
                it.name.equals(limpio, ignoreCase = true) && it.type == type
            }
            if (yaExiste) {
                _error.value = "Ya tienes una categoría llamada \"$limpio\"."
                return@launch
            }

            // sortOrder al final del grupo, para que las nuevas no se cuelen
            // entre las predeterminadas y desordenen una lista ya conocida.
            val ultima = categories.value.filter { it.type == type }.maxOfOrNull { it.sortOrder } ?: 0

            categoryDao.insert(
                CategoryEntity(
                    name = limpio,
                    type = type,
                    iconName = iconName,
                    isCustom = true,
                    sortOrder = ultima + 10
                )
            )
            _error.value = null
            refreshImpacts()
        }
    }

    fun update(id: Long, name: String, iconName: String) {
        val limpio = name.trim()
        if (limpio.isBlank()) {
            _error.value = "La categoría necesita un nombre."
            return
        }

        viewModelScope.launch {
            val actual = categoryDao.getById(id) ?: return@launch
            categoryDao.update(actual.copy(name = limpio, iconName = iconName))
            _error.value = null
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            // La lapida va antes del borrado; si no, el proximo pull traeria la
            // categoria de vuelta desde la nube. Ver Tombstones.
            tombstones.antesDeBorrarCategoria(id)
            categoryDao.deleteById(id)
            _error.value = null
            refreshImpacts()
        }
    }

    fun consumeError() {
        _error.value = null
    }
}
