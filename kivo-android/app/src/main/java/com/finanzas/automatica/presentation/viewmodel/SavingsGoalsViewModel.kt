package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.dao.SavingsGoalDao
import com.finanzas.automatica.data.local.entity.AppNotificationEntity
import com.finanzas.automatica.data.repository.AppNotificationRepository
import com.finanzas.automatica.domain.enrichment.toDomain
import com.finanzas.automatica.domain.enrichment.toDomainSafely
import com.finanzas.automatica.domain.enrichment.toEntity
import com.finanzas.automatica.domain.model.SavingsGoal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavingsGoalsViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val goalDao = database.savingsGoalDao()
    private val notifications = AppNotificationRepository(database)

    // Reactivo sobre Room: crear/editar/abonar/eliminar una meta desde la pantalla de
    // edicion (que abre su propia instancia de este ViewModel, ver AppNavHost) se ve
    // al instante en la lista tambien (misma correccion que MovementViewModel.movements).
    val goals: StateFlow<List<SavingsGoal>> = goalDao.getAllFlow()
        .map { entities -> entities.toDomainSafely { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun addGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            goalDao.insert(goal.toEntity())
        }
    }

    fun updateGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            goalDao.update(goal.toEntity())
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            goalDao.deleteById(id)
        }
    }
    
    /**
     * Abona [amount] centavos a la meta [id]. `SavingsGoalDao.updateProgress` reemplaza
     * currentAmount (no lo incrementa), así que aquí se calcula el nuevo total sumando
     * al monto ya ahorrado -- antes este método sobrescribía el ahorro previo con el
     * abono nuevo en vez de sumarlo.
     */
    fun addProgress(id: Long, amount: Long) {
        viewModelScope.launch {
            val goal = goalDao.getById(id)?.toDomain() ?: return@launch
            val previousAmount = goal.currentAmount
            val newAmount = previousAmount + amount
            goalDao.updateProgress(id, newAmount, System.currentTimeMillis())
            if (newAmount >= goal.targetAmount && previousAmount < goal.targetAmount) {
                notifications.notify(
                    type = AppNotificationEntity.TYPE_GOAL,
                    title = "Meta lograda",
                    message = "Felicitaciones, alcanzaste la meta \"${goal.name}\"."
                )
            }
        }
    }
}
