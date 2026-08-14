package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.dao.BudgetDao
import com.finanzas.automatica.data.local.entity.AppNotificationEntity
import com.finanzas.automatica.data.repository.AppNotificationRepository
import com.finanzas.automatica.data.repository.CategoryRepositoryImpl
import com.finanzas.automatica.data.repository.MovementRepositoryImpl
import com.finanzas.automatica.domain.enrichment.toDomain
import com.finanzas.automatica.domain.enrichment.toEntity
import com.finanzas.automatica.domain.model.Budget
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.domain.model.MovementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

/** Misma clave usada para identificar un presupuesto en la lista (ver BudgetsScreen). */
fun budgetKey(budget: Budget): String = budget.id?.toString() ?: "${budget.categoryId}-${budget.month}-${budget.year}"

class BudgetsViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val budgetDao = database.budgetDao()
    private val categoryRepo = CategoryRepositoryImpl(database)
    private val movementRepository = MovementRepositoryImpl(database)
    private val notifications = AppNotificationRepository(database)

    // Reactivo sobre Room: crear/editar/eliminar un presupuesto desde la pantalla de
    // edicion (que abre su propia instancia de este ViewModel, ver AppNavHost) se ve
    // al instante en la lista tambien, sin refrescar nada a mano ni reiniciar la app
    // (misma correccion que MovementViewModel.movements, ver esa clase).
    val budgets: StateFlow<List<Budget>> = budgetDao.getAllFlow()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Gasto real por presupuesto (clave = budgetKey), reactivo sobre Room: se
    // recalcula solo al confirmar/rechazar un movimiento o cambiar la lista de
    // presupuestos, sin recargar nada manualmente. Antes BudgetsScreen mostraba
    // siempre 0 gastado porque nunca se conectó a los movimientos reales.
    val spentByBudgetKey: StateFlow<Map<String, Long>> = budgets
        .flatMapLatest { list ->
            if (list.isEmpty()) {
                flowOf(emptyMap())
            } else {
                combine(
                    list.map { budget ->
                        val (start, end) = monthRange(budget.month, budget.year)
                        movementRepository.getSpentByCategoryAndDateRangeFlow(budget.categoryId, start, end)
                            .map { spent -> budgetKey(budget) to (spent ?: 0L) }
                    }
                ) { pairs -> pairs.toMap() }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    init {
        loadExpenseCategories()
    }

    private fun monthRange(month: Int, year: Int): Pair<Long, Long> {
        val zone = ZoneId.systemDefault()
        val start = LocalDate.of(year, month, 1).atStartOfDay(zone).toInstant().toEpochMilli()
        val end = LocalDate.of(year, month, 1).plusMonths(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
        return start to end
    }

    fun loadExpenseCategories() {
        viewModelScope.launch {
            try {
                _categories.value = categoryRepo.getByType(MovementType.EXPENSE.name).map { it.toDomain() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun addBudget(budget: Budget) {
        viewModelScope.launch {
            budgetDao.insert(budget.toEntity())
            warnIfAlreadyOverLimit(budget)
        }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            budgetDao.update(budget.toEntity())
            warnIfAlreadyOverLimit(budget)
        }
    }

    private suspend fun warnIfAlreadyOverLimit(budget: Budget) {
        val category = categoryRepo.getById(budget.categoryId)?.toDomain() ?: return
        val now = java.time.LocalDate.now()
        val start = now.withDayOfMonth(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = now.atTime(java.time.LocalTime.MAX).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val spent = database.movementDao()
            .getByCategoryAndDateRange(budget.categoryId, start, end)
            .sumOf { it.amount }
        if (spent >= budget.monthlyLimit) {
            notifications.notify(
                type = AppNotificationEntity.TYPE_BUDGET,
                title = "Presupuesto ajustado",
                message = "El plan de ${category.name} ya está por debajo de lo gastado este mes. Revisa tus gastos."
            )
        }
    }
    
    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            budgetDao.deleteById(id)
        }
    }
}
