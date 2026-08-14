package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.dao.SavingsGoalDao
import com.finanzas.automatica.data.local.entity.AppNotificationEntity
import com.finanzas.automatica.data.repository.AppNotificationRepository
import com.finanzas.automatica.domain.enrichment.toDomain
import com.finanzas.automatica.domain.enrichment.toEntity
import com.finanzas.automatica.domain.model.SavingsGoal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SavingsGoalsViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val goalDao = database.savingsGoalDao()
    private val notifications = AppNotificationRepository(database)
    
    private val _goals = MutableStateFlow<List<SavingsGoal>>(emptyList())
    val goals: StateFlow<List<SavingsGoal>> = _goals
    
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    init {
        loadGoals()
    }
    
    fun loadGoals() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _goals.value = goalDao.getAll().map { it.toDomain() }
            } catch (e: Exception) {
                e.printStackTrace()
                _goals.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            goalDao.insert(goal.toEntity())
            loadGoals()
        }
    }
    
    fun updateGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            goalDao.update(goal.toEntity())
            loadGoals()
        }
    }
    
    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            goalDao.deleteById(id)
            loadGoals()
        }
    }
    
    fun addProgress(id: Long, amount: Long) {
        viewModelScope.launch {
            goalDao.updateProgress(id, amount, System.currentTimeMillis())
            val goal = goalDao.getById(id)?.toDomain()
            if (goal != null && goal.currentAmount >= goal.targetAmount && goal.currentAmount - amount < goal.targetAmount) {
                notifications.notify(
                    type = AppNotificationEntity.TYPE_GOAL,
                    title = "Meta lograda",
                    message = "Felicitaciones, alcanzaste la meta \"${goal.name}\"."
                )
            }
            loadGoals()
        }
    }
}
