package com.finanzas.automatica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finanzas.automatica.data.local.entity.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsGoalDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(goal: SavingsGoalEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(goals: List<SavingsGoalEntity>): List<Long>

    @Update
    suspend fun update(goal: SavingsGoalEntity): Int

    @Query("SELECT * FROM savings_goals WHERE id = :id")
    suspend fun getById(id: Long): SavingsGoalEntity?

    @Query("SELECT * FROM savings_goals ORDER BY targetDate ASC")
    suspend fun getAll(): List<SavingsGoalEntity>

    @Query("SELECT * FROM savings_goals ORDER BY targetDate ASC")
    fun getAllFlow(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals WHERE targetDate >= :now ORDER BY targetDate ASC")
    suspend fun getActive(now: Long): List<SavingsGoalEntity>

    @Query("DELETE FROM savings_goals WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("UPDATE savings_goals SET currentAmount = :amount, updatedAt = :now WHERE id = :id")
    suspend fun updateProgress(id: Long, amount: Long, now: Long): Int
}