package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.repository.MovementRepositoryImpl
import com.finanzas.automatica.domain.enrichment.toDomain
import com.finanzas.automatica.domain.model.Movement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovementViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val repository = MovementRepositoryImpl(database)
    
    private val _movements = MutableStateFlow<List<Movement>>(emptyList())
    val movements: StateFlow<List<Movement>> = _movements
    
    private val _pendingCount = MutableStateFlow<Int>(0)
    val pendingCount: StateFlow<Int> = _pendingCount
    
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadMovements()
        loadPendingCount()
    }

    fun loadMovements(limit: Int = 50, offset: Int = 0) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val entities = repository.getPaginated(limit, offset)
                _movements.value = entities.map { it.toDomain() }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPendingCount() {
        viewModelScope.launch {
            val count = repository.countPending()
            _pendingCount.value = count
        }
    }

    fun confirmMovement(id: Long) {
        viewModelScope.launch {
            repository.updateConfirmationState(id, "CONFIRMED")
            loadMovements()
            loadPendingCount()
        }
    }

    fun rejectMovement(id: Long) {
        viewModelScope.launch {
            repository.updateConfirmationState(id, "REJECTED")
            loadMovements()
            loadPendingCount()
        }
    }

    fun correctMovement(id: Long, newCategoryId: Long) {
        viewModelScope.launch {
            repository.updateCategory(id, newCategoryId)
            repository.updateConfirmationState(id, "CORRECTED")
            loadMovements()
            loadPendingCount()
        }
    }
}
