package com.finanzas.automatica.data.repository

import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.dao.MovementDao
import com.finanzas.automatica.data.local.entity.MovementEntity
import kotlinx.coroutines.flow.Flow

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

    suspend fun getPaginated(limit: Int, offset: Int): List<MovementEntity> {
        return dao.getPaginated(limit, offset)
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

    suspend fun getByType(type: String): List<MovementEntity> {
        return dao.getByType(type)
    }

    suspend fun getByCounterparty(counterpartyId: Long): List<MovementEntity> {
        return dao.getByCounterparty(counterpartyId)
    }

    suspend fun getByCategory(categoryId: Long): List<MovementEntity> {
        return dao.getByCategory(categoryId)
    }

    suspend fun getByBankEntity(bankEntity: String): List<MovementEntity> {
        return dao.getByBankEntity(bankEntity)
    }

    suspend fun count(): Int {
        return dao.count()
    }

    suspend fun countPending(): Int {
        return dao.countPending()
    }

    suspend fun getTotalIncome(startDate: Long, endDate: Long): Long? {
        return dao.sumIncome(startDate, endDate)
    }

    suspend fun getTotalExpense(startDate: Long, endDate: Long): Long? {
        return dao.sumExpense(startDate, endDate)
    }

    suspend fun getExpensesByCategory(startDate: Long, endDate: Long): List<MovementDao.CategoryTotal> {
        return dao.getExpensesByCategory(startDate, endDate)
    }

    suspend fun getMonthlyExpenseTotals(): List<MovementDao.MonthlyTotal> {
        return dao.getMonthlyExpenseTotals()
    }

    suspend fun getMonthlyIncomeTotals(): List<MovementDao.MonthlyTotal> {
        return dao.getMonthlyIncomeTotals()
    }

    suspend fun deleteById(id: Long): Int {
        return dao.deleteById(id)
    }

    suspend fun deleteOlderThan(beforeDate: Long): Int {
        return dao.deleteOlderThan(beforeDate)
    }
}