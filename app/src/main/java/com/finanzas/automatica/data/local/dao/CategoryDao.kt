package com.finanzas.automatica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finanzas.automatica.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>): List<Long>

    @Update
    suspend fun update(category: CategoryEntity): Int

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY sortOrder ASC, name ASC")
    suspend fun getByType(type: String): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY sortOrder ASC, name ASC")
    fun getByTypeFlow(type: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY type ASC, sortOrder ASC, name ASC")
    suspend fun getAll(): List<CategoryEntity>

    @Query("SELECT * FROM categories ORDER BY type ASC, sortOrder ASC, name ASC")
    fun getAllFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE parentCategoryId = :parentId")
    suspend fun getChildren(parentId: Long): List<CategoryEntity>

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT COUNT(*) FROM categories WHERE isCustom = 1")
    suspend fun countCustom(): Int
}