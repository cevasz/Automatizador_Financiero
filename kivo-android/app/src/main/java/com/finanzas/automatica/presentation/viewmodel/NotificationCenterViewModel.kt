package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.entity.AppNotificationEntity
import com.finanzas.automatica.data.repository.AppNotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationCenterViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val repository = AppNotificationRepository(database)

    private val _notifications = MutableStateFlow<List<AppNotificationEntity>>(emptyList())
    val notifications: StateFlow<List<AppNotificationEntity>> = _notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    init {
        viewModelScope.launch {
            repository.observeAll().collect { _notifications.value = it }
        }
        viewModelScope.launch {
            repository.observeUnreadCount().collect { _unreadCount.value = it }
        }
    }

    fun markRead(id: Long) {
        viewModelScope.launch { repository.markRead(id) }
    }

    fun markAllRead() {
        viewModelScope.launch { repository.markAllRead() }
    }
}
