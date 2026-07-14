package com.finanzas.automatica.domain.enrichment

import com.finanzas.automatica.data.local.entity.AgendaEntryEntity
import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.domain.model.RawMovement
import kotlinx.coroutines.flow.Flow

interface AgendaRepository {
    suspend fun findByIdentifier(identifier: String): AgendaEntryEntity?
    suspend fun findAll(): List<AgendaEntryEntity>
    fun observeAll(): Flow<List<AgendaEntryEntity>>
    suspend fun insert(entry: AgendaEntryEntity): Long
    suspend fun update(entry: AgendaEntryEntity)
    suspend fun delete(id: Long)
}