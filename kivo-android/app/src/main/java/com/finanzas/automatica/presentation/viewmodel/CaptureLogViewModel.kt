package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.entity.CaptureLogEntity
import com.finanzas.automatica.data.repository.CaptureLogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CaptureLogViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val repository = CaptureLogRepository(database)

    private val _entries = MutableStateFlow<List<CaptureLogEntity>>(emptyList())
    val entries: StateFlow<List<CaptureLogEntity>> = _entries

    /** Solo lo que NO se registró: es lo que el usuario viene a revisar. */
    private val _onlyIgnored = MutableStateFlow(false)
    val onlyIgnored: StateFlow<Boolean> = _onlyIgnored

    init {
        viewModelScope.launch {
            repository.observeRecent().collect { _entries.value = it }
        }
    }

    fun setOnlyIgnored(value: Boolean) {
        _onlyIgnored.value = value
    }

    fun clear() {
        viewModelScope.launch { repository.clear() }
    }
}
