package com.finanzas.automatica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finanzas.automatica.data.local.entity.SyncDeletionEntity

@Dao
interface SyncDeletionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deletion: SyncDeletionEntity)

    @Query("SELECT * FROM sync_deletions ORDER BY deletedAt ASC")
    suspend fun getAll(): List<SyncDeletionEntity>

    @Query("DELETE FROM sync_deletions WHERE syncId IN (:syncIds)")
    suspend fun deleteBySyncIds(syncIds: List<String>): Int

    @Query("DELETE FROM sync_deletions")
    suspend fun deleteAll(): Int

    @Query("SELECT COUNT(*) FROM sync_deletions")
    suspend fun count(): Int
}
