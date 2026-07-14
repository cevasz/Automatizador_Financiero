package com.finanzas.automatica.data.repository

import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.dao.MovementDao
import com.finanzas.automatica.data.local.entity.MovementEntity
import com.finanzas.automatica.domain.enrichment.MovementRepository
import kotlinx.coroutines.flow.Flow

class MovementRepositoryImpl(
    private val dao: MovementDao
) : MovementRepository {

    override suspend fun insert(movement: MovementEntity): Long {
        return dao.insert(movement)
    }

    override suspend fun update(movement: MovementEntity) {
        dao.update(movement)
    }

    override fun getPendingConfirmation(): Flow<List<MovementEntity>> {
        return dao.getByConfirmationStateFlow("PENDING")
    }
}