package com.finanzas.automatica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finanzas.automatica.data.local.entity.AgendaEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgendaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: AgendaEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entries: List<AgendaEntryEntity>): List<Long>

    @Update
    suspend fun update(entry: AgendaEntryEntity): Int

    @Query("SELECT * FROM agenda_entries WHERE id = :id")
    suspend fun getById(id: Long): AgendaEntryEntity?

    @Query("SELECT * FROM agenda_entries WHERE accountIdentifier = :identifier")
    suspend fun getByIdentifier(identifier: String): AgendaEntryEntity?

    @Query("SELECT * FROM agenda_entries ORDER BY displayName ASC")
    suspend fun getAll(): List<AgendaEntryEntity>

    @Query("SELECT * FROM agenda_entries ORDER BY displayName ASC")
    fun getAllFlow(): Flow<List<AgendaEntryEntity>>

    @Query("SELECT * FROM agenda_entries WHERE defaultCategoryId = :categoryId")
    suspend fun getByCategory(categoryId: Long): List<AgendaEntryEntity>

    @Query("DELETE FROM agenda_entries WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT COUNT(*) FROM agenda_entries")
    suspend fun count(): Int
}