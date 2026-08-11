package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.repository.AgendaRepositoryImpl
import com.finanzas.automatica.data.repository.CategoryRepositoryImpl
import com.finanzas.automatica.domain.enrichment.toDomain
import com.finanzas.automatica.domain.enrichment.toEntity
import com.finanzas.automatica.domain.model.AgendaEntry
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.domain.model.MovementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AgendaViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val agendaRepo = AgendaRepositoryImpl(database)
    private val categoryRepo = CategoryRepositoryImpl(database)
    
    private val _entries = MutableStateFlow<List<AgendaEntry>>(emptyList())
    val entries: StateFlow<List<AgendaEntry>> = _entries
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories
    
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    init {
        loadEntries()
        loadCategories()
    }
    
    fun loadEntries() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _entries.value = agendaRepo.findAll().map { it.toDomain() }
            } catch (e: Exception) {
                e.printStackTrace()
                _entries.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = categoryRepo.getAll().map { it.toDomain() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun addEntry(entry: AgendaEntry) {
        viewModelScope.launch {
            agendaRepo.insert(entry.toEntity())
            loadEntries()
        }
    }
    
    fun updateEntry(entry: AgendaEntry) {
        viewModelScope.launch {
            agendaRepo.update(entry.toEntity())
            loadEntries()
        }
    }
    
    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            agendaRepo.delete(id)
            loadEntries()
        }
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun getCategoriesByType(type: MovementType): List<Category> {
        return _categories.value.filter { it.type == type }
    }
}
