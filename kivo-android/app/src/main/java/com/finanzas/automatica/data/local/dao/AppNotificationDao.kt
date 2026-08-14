package com.finanzas.automatica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.finanzas.automatica.data.local.entity.AppNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppNotificationDao {

    @Insert
    suspend fun insert(notification: AppNotificationEntity): Long

    @Query("SELECT * FROM app_notifications ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<AppNotificationEntity>>

    @Query("SELECT * FROM app_notifications ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<AppNotificationEntity>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE read = 0")
    fun unreadCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE read = 0")
    suspend fun countUnread(): Int

    @Query("UPDATE app_notifications SET read = 1 WHERE id = :id")
    suspend fun markRead(id: Long)

    @Query("UPDATE app_notifications SET read = 1")
    suspend fun markAllRead()

    @Query("DELETE FROM app_notifications")
    suspend fun deleteAll()
}
