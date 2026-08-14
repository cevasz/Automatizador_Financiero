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

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId")
    suspend fun getByCategory(categoryId: Long): List<BudgetEntity>

    // Usado por DefaultCategories.dedupe() para reapuntar un presupuesto individual a la
    // categoria "canonica" al fusionar categorias duplicadas. Se actualiza de a un
    // presupuesto por vez (no en bloque) porque el indice unico (categoryId, month, year)
    // puede rechazar la actualizacion si la categoria canonica ya tiene un presupuesto
    // para ese mismo mes/año -- el llamador debe capturar esa excepcion y decidir
    // (ver DefaultCategories.dedupe).
    @Query("UPDATE budgets SET categoryId = :newCategoryId WHERE id = :budgetId")
    suspend fun reassignSingle(budgetId: Long, newCategoryId: Long): Int

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year ORDER BY categoryId ASC")
    suspend fun getByPeriod(month: Int, year: Int): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year ORDER BY categoryId ASC")
    fun getByPeriodFlow(month: Int, year: Int): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets ORDER BY year DESC, month DESC, categoryId ASC")
    suspend fun getAll(): List<BudgetEntity>

    // Reactivo sobre Room: usado por BudgetsViewModel para que crear/editar/eliminar
    // un presupuesto desde cualquier pantalla se refleje al instante en todas las
    // demas (misma razon que MovementDao.getAllFlow(), ver esa clase).
    @Query("SELECT * FROM budgets ORDER BY year DESC, month DESC, categoryId ASC")
    fun getAllFlow(): Flow<List<BudgetEntity>>

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM budgets")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM budgets WHERE categoryId = :categoryId AND month = :month AND year = :year")
    suspend fun deleteByCategoryAndPeriod(categoryId: Long, month: Int, year: Int): Int
}
