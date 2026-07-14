package com.finanzas.automatica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finanzas.automatica.data.local.entity.AgendaEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface AgendaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: AgendaEntry): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entries: List<AgendaEntry>): List<Long>

    @Update
    suspend fun update(entry: AgendaEntry): Int

    @Query("SELECT * FROM agenda_entries WHERE id = :id")
    suspend fun getById(id: Long): AgendaEntry?

    @Query("SELECT * FROM agenda_entries WHERE accountIdentifier = :identifier")
    suspend fun getByIdentifier(identifier: String): AgendaEntry?

    @Query("SELECT * FROM agenda_entries ORDER BY displayName ASC")
    suspend fun getAll(): List<AgendaEntry>

    @Query("SELECT * FROM agenda_entries ORDER BY displayName ASC")
    fun getAllFlow(): Flow<List<AgendaEntry>>

    @Query("SELECT * FROM agenda_entries WHERE defaultCategoryId = :categoryId")
    suspend fun getByCategory(categoryId: Long): List<AgendaEntry>

    @Query("DELETE FROM agenda_entries WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT COUNT(*) FROM agenda_entries")
    suspend fun count(): Int
}