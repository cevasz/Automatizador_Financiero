package com.finanzas.automatica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finanzas.automatica.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgets: List<BudgetEntity>): List<Long>

    @Update
    suspend fun update(budget: BudgetEntity): Int

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getById(id: Long): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND month = :month AND year = :year")
    suspend fun getByCategoryAndPeriod(categoryId: Long, month: Int, year: Int): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year ORDER BY categoryId ASC")
    suspend fun getByPeriod(month: Int, year: Int): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year ORDER BY categoryId ASC")
    fun getByPeriodFlow(month: Int, year: Int): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets ORDER BY year DESC, month DESC, categoryId ASC")
    suspend fun getAll(): List<BudgetEntity>

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM budgets WHERE categoryId = :categoryId AND month = :month AND year = :year")
    suspend fun deleteByCategoryAndPeriod(categoryId: Long, month: Int, year: Int): Int
}