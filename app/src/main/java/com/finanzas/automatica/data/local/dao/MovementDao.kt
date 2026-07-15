package com.finanzas.automatica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finanzas.automatica.data.local.entity.MovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movement: MovementEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(movements: List<MovementEntity>): List<Long>

    @Update
    suspend fun update(movement: MovementEntity): Int

    @Query("SELECT * FROM movements WHERE id = :id")
    suspend fun getById(id: Long): MovementEntity?

    @Query("SELECT * FROM movements ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getPaginated(limit: Int, offset: Int): List<MovementEntity>

    @Query("SELECT * FROM movements WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getByDateRange(startDate: Long, endDate: Long): List<MovementEntity>

    @Query("SELECT * FROM movements WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getByDateRangeFlow(startDate: Long, endDate: Long): Flow<List<MovementEntity>>

    @Query("SELECT * FROM movements WHERE type = :type AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getByTypeAndDateRange(type: String, startDate: Long, endDate: Long): List<MovementEntity>

    @Query("SELECT * FROM movements WHERE type = :type ORDER BY date DESC")
    suspend fun getByType(type: String): List<MovementEntity>

    @Query("SELECT * FROM movements WHERE bankEntity = :entity ORDER BY date DESC")
    suspend fun getByBankEntity(entity: String): List<MovementEntity>

    @Query("SELECT * FROM movements WHERE confirmationState = :state ORDER BY date DESC")
    suspend fun getByConfirmationState(state: String): List<MovementEntity>

    @Query("SELECT * FROM movements WHERE confirmationState = :state ORDER BY date DESC")
    fun getByConfirmationStateFlow(state: String): Flow<List<MovementEntity>>

    @Query("SELECT * FROM movements WHERE counterpartyId = :counterpartyId ORDER BY date DESC")
    suspend fun getByCounterparty(counterpartyId: Long): List<MovementEntity>

    @Query("SELECT * FROM movements WHERE categoryId = :categoryId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getByCategoryAndDateRange(categoryId: Long, startDate: Long, endDate: Long): List<MovementEntity>

    @Query("SELECT * FROM movements WHERE categoryId = :categoryId ORDER BY date DESC")
    suspend fun getByCategory(categoryId: Long): List<MovementEntity>

    // Aggregations for dashboard
    @Query("SELECT SUM(amount) FROM movements WHERE type = 'INCOME' AND date >= :startDate AND date <= :endDate")
    suspend fun getTotalIncome(startDate: Long, endDate: Long): Long?

    @Query("SELECT SUM(amount) FROM movements WHERE type = 'EXPENSE' AND date >= :startDate AND date <= :endDate")
    suspend fun getTotalExpense(startDate: Long, endDate: Long): Long?

    @Query("SELECT categoryId, SUM(amount) as total FROM movements WHERE type = 'EXPENSE' AND date >= :startDate AND date <= :endDate GROUP BY categoryId ORDER BY total DESC")
    suspend fun getExpensesByCategory(startDate: Long, endDate: Long): List<CategoryTotal>

    @Query("SELECT strftime('%Y-%m', date/1000, 'unixepoch') as month, SUM(amount) as total FROM movements WHERE type = 'EXPENSE' GROUP BY month ORDER BY month DESC LIMIT 12")
    suspend fun getMonthlyExpenseTotals(): List<MonthlyTotal>

    @Query("SELECT strftime('%Y-%m', date/1000, 'unixepoch') as month, SUM(amount) as total FROM movements WHERE type = 'INCOME' GROUP BY month ORDER BY month DESC LIMIT 12")
    suspend fun getMonthlyIncomeTotals(): List<MonthlyTotal>

    @Query("SELECT COUNT(*) FROM movements")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM movements WHERE confirmationState = 'PENDING'")
    suspend fun countPending(): Int

    @Query("DELETE FROM movements WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM movements WHERE date < :beforeDate")
    suspend fun deleteOlderThan(beforeDate: Long): Int
}

data class CategoryTotal(
    val categoryId: Long,
    val total: Long
)

data class MonthlyTotal(
    val month: String, // YYYY-MM
    val total: Long
)