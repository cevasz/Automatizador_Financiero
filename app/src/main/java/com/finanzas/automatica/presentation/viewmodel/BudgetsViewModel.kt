package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.repository.CategoryRepositoryImpl
import com.finanzas.automatica.data.local.dao.BudgetDao
import com.finanzas.automatica.domain.model.Budget
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.domain.model.MovementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BudgetsViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val budgetDao = database.budgetDao()
    private val categoryRepo = CategoryRepositoryImpl(database)
    
    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories
    
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    init {
        loadBudgets()
        loadExpenseCategories()
    }
    
    fun loadBudgets() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _budgets.value = budgetDao.getAll().map { it.toDomain() }
            } finally {
                _isLoading.value = false
            }
        }
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
            loadBudgets()
        }
    }
    
    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            budgetDao.update(budget.toEntity())
            loadBudgets()
        }
    }
    
    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            budgetDao.deleteById(id)
            loadBudgets()
        }
    }
}