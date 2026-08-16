package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.repository.AgendaRepositoryImpl
import com.finanzas.automatica.data.repository.CategoryRepositoryImpl
import com.finanzas.automatica.domain.enrichment.toDomain
import com.finanzas.automatica.domain.enrichment.toDomainSafely
import com.finanzas.automatica.domain.enrichment.toEntity
import com.finanzas.automatica.domain.model.AgendaEntry
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.domain.model.MovementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AgendaViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val agendaRepo = AgendaRepositoryImpl(database)
    private val categoryRepo = CategoryRepositoryImpl(database)

    // Reactivo sobre Room: crear/editar/eliminar un contacto desde la pantalla de
    // edicion (que abre su propia instancia de este ViewModel, ver AppNavHost) se ve
    // al instante en la lista tambien (misma correccion que MovementViewModel.movements).
    val entries: StateFlow<List<AgendaEntry>> = agendaRepo.observeAll()
        .map { entities -> entities.toDomainSafely { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = categoryRepo.getAll().toDomainSafely { it.toDomain() }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }
    
    fun addEntry(entry: AgendaEntry) {
        viewModelScope.launch {
            agendaRepo.insert(entry.toEntity())
        }
    }

    fun updateEntry(entry: AgendaEntry) {
        viewModelScope.launch {
            agendaRepo.update(entry.toEntity())
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            agendaRepo.delete(id)
        }
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun getCategoriesByType(type: MovementType): List<Category> {
        return _categories.value.filter { it.type == type }
    }
}
