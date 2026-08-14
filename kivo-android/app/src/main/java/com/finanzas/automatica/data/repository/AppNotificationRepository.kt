package com.finanzas.automatica.data.repository

import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.entity.AppNotificationEntity
import kotlinx.coroutines.flow.Flow

class AppNotificationRepository(
    private val database: FinanzasDatabase
) {
    private val dao = database.appNotificationDao()

    fun observeAll(): Flow<List<AppNotificationEntity>> = dao.getAllFlow()

    fun observeUnreadCount(): Flow<Int> = dao.unreadCountFlow()

    suspend fun notify(type: String, title: String, message: String) {
        dao.insert(AppNotificationEntity(type = type, title = title, message = message))
    }

    suspend fun markRead(id: Long) = dao.markRead(id)

    suspend fun markAllRead() = dao.markAllRead()

    suspend fun countUnread(): Int = dao.countUnread()
}
