package com.finanzas.automatica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finanzas.automatica.data.local.entity.ClassificationRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassificationRuleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(rule: ClassificationRuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(rules: List<ClassificationRuleEntity>): List<Long>

    @Update
    suspend fun update(rule: ClassificationRuleEntity): Int

    @Query("SELECT * FROM classification_rules WHERE id = :id")
    suspend fun getById(id: Long): ClassificationRuleEntity?

    @Query("SELECT * FROM classification_rules WHERE bankEntity = :bankEntity AND isActive = 1 ORDER BY priority DESC")
    suspend fun getActiveByBank(bankEntity: String): List<ClassificationRuleEntity>

    @Query("SELECT * FROM classification_rules WHERE bankEntity = :bankEntity AND isActive = 1 ORDER BY priority DESC")
    fun getActiveByBankFlow(bankEntity: String): Flow<List<ClassificationRuleEntity>>

    @Query("SELECT * FROM classification_rules ORDER BY bankEntity ASC, priority DESC")
    suspend fun getAll(): List<ClassificationRuleEntity>

    @Query("DELETE FROM classification_rules WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("UPDATE classification_rules SET isActive = :active WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean): Int
}