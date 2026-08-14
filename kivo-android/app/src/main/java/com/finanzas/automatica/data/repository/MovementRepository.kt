package com.finanzas.automatica.data.repository

import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.dao.MovementDao
import com.finanzas.automatica.data.local.dao.CategoryTotal
import com.finanzas.automatica.data.local.dao.MonthlyTotal
import com.finanzas.automatica.data.local.entity.MovementEntity
import kotlinx.coroutines.flow.Flow
import java.time.Instant

class MovementRepositoryImpl(
    private val database: FinanzasDatabase
) {
    private val dao = database.movementDao()

    suspend fun insert(movement: MovementEntity): Long {
        return dao.insert(movement)
    }

    suspend fun insertAll(movements: List<MovementEntity>): List<Long> {
        return dao.insertAll(movements)
    }

    suspend fun update(movement: MovementEntity): Int {
        return dao.update(movement)
    }

    suspend fun getById(id: Long): MovementEntity? {
        return dao.getById(id)
    }

    suspend fun getAll(): List<MovementEntity> {
        return dao.getAll()
    }

    suspend fun getPaginated(limit: Int, offset: Int): List<MovementEntity> {
        return dao.getPaginated(limit, offset)
    }

    fun getAllFlow(): Flow<List<MovementEntity>> {
        return dao.getAllFlow()
    }

    suspend fun getByDateRange(startDate: Long, endDate: Long): List<MovementEntity> {
        return dao.getByDateRange(startDate, endDate)
    }

    fun getByDateRangeFlow(startDate: Long, endDate: Long): Flow<List<MovementEntity>> {
        return dao.getByDateRangeFlow(startDate, endDate)
    }

    suspend fun getByConfirmationState(state: String): List<MovementEntity> {
        return dao.getByConfirmationState(state)
    }

    fun getByConfirmationStateFlow(state: String): Flow<List<MovementEntity>> {
        return dao.getByConfirmationStateFlow(state)
    }

    suspend fun getByTypeAndDateRange(type: String, startDate: Long, endDate: Long): List<MovementEntity> {
        return dao.getByTypeAndDateRange(type, startDate, endDate)
    }

    suspend fun getByCounterparty(counterpartyId: Long): List<MovementEntity> {
        return dao.getByCounterparty(counterpartyId)
    }

    suspend fun getByCounterpartyRaw(counterpartyRaw: String): List<MovementEntity> {
        return dao.getByCounterpartyRaw(counterpartyRaw)
    }

    suspend fun getByCategoryAndDateRange(categoryId: Long, startDate: Long, endDate: Long): List<MovementEntity> {
        return dao.getByCategoryAndDateRange(categoryId, startDate, endDate)
    }

    fun getSpentByCategoryAndDateRangeFlow(categoryId: Long, startDate: Long, endDate: Long): Flow<Long?> {
        return dao.getSpentByCategoryAndDateRangeFlow(categoryId, startDate, endDate)
    }

    suspend fun getByBankEntity(bankEntity: String): List<MovementEntity> {
        return dao.getByBankEntity(bankEntity)
    }

    suspend fun countPending(): Int {
        return dao.countPending()
    }

    suspend fun getTotalIncome(startDate: Long, endDate: Long): Long? {
        return dao.getTotalIncome(startDate, endDate)
    }

    suspend fun getTotalExpense(startDate: Long, endDate: Long): Long? {
        return dao.getTotalExpense(startDate, endDate)
    }

    suspend fun getExpensesByCategory(startDate: Long, endDate: Long): List<CategoryTotal> {
        return dao.getExpensesByCategory(startDate, endDate)
    }

    suspend fun getMonthlyExpenseTotals(): List<MonthlyTotal> {
        return dao.getMonthlyExpenseTotals()
    }

    suspend fun getMonthlyIncomeTotals(): List<MonthlyTotal> {
        return dao.getMonthlyIncomeTotals()
    }

    suspend fun deleteById(id: Long): Int {
        return dao.deleteById(id)
    }

    suspend fun updateConfirmationState(id: Long, state: String) {
        dao.updateConfirmationState(id, state, Instant.now().toEpochMilli())
    }

    suspend fun updateCategory(id: Long, categoryId: Long) {
        dao.updateCategory(id, categoryId, Instant.now().toEpochMilli())
    }
}
