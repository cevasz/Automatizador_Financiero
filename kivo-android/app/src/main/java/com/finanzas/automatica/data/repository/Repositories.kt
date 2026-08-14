package com.finanzas.automatica.data.repository

import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.dao.AgendaDao
import com.finanzas.automatica.data.local.dao.CategoryDao
import com.finanzas.automatica.data.local.entity.AgendaEntryEntity
import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.domain.enrichment.AgendaRepository
import kotlinx.coroutines.flow.Flow

class AgendaRepositoryImpl(
    private val database: FinanzasDatabase
) : AgendaRepository {

    private val dao = database.agendaDao()

    override suspend fun findByIdentifier(identifier: String): AgendaEntryEntity? {
        return dao.getByIdentifier(identifier)
    }

    override suspend fun findAll(): List<AgendaEntryEntity> {
        return dao.getAll()
    }

    override fun observeAll(): Flow<List<AgendaEntryEntity>> {
        return dao.getAllFlow()
    }

    override suspend fun insert(entry: AgendaEntryEntity): Long {
        return dao.insert(entry)
    }

    override suspend fun update(entry: AgendaEntryEntity) {
        dao.update(entry)
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }
}

class CategoryRepositoryImpl(
    private val database: FinanzasDatabase
) {
    private val dao = database.categoryDao()

    suspend fun getById(id: Long): CategoryEntity? {
        return dao.getById(id)
    }

    suspend fun getByType(type: String): List<CategoryEntity> {
        return dao.getByType(type)
    }

    fun getByTypeFlow(type: String): Flow<List<CategoryEntity>> {
        return dao.getByTypeFlow(type)
    }

    suspend fun getAll(): List<CategoryEntity> {
        return dao.getAll()
    }

    fun getAllFlow(): Flow<List<CategoryEntity>> {
        return dao.getAllFlow()
    }

    suspend fun insert(category: CategoryEntity): Long {
        return dao.insert(category)
    }

    suspend fun insertAll(categories: List<CategoryEntity>): List<Long> {
        return dao.insertAll(categories)
    }

    suspend fun update(category: CategoryEntity) {
        dao.update(category)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }
}