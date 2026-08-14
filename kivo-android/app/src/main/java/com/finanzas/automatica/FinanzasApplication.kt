package com.finanzas.automatica

import android.app.Application
import androidx.room.Room
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.repository.DefaultCategories
import com.finanzas.automatica.domain.enrichment.ClassificationRepositoryProvider
import com.finanzas.automatica.domain.enrichment.RoomCategoryLookupRepository
import com.finanzas.automatica.domain.enrichment.RoomMovementHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FinanzasApplication : Application() {

    private var database: FinanzasDatabase? = null

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            FinanzasDatabase::class.java,
            "finanzas.db"
        ).fallbackToDestructiveMigration()
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            DefaultCategories.seed(database!!)
            // Limpia categorias duplicadas dejadas por versiones anteriores (seed() se
            // ejecutaba en cada arranque sin verificar si ya existian). Idempotente.
            DefaultCategories.dedupe(database!!)
        }

        ClassificationRepositoryProvider.categoryLookupRepository =
            RoomCategoryLookupRepository(database!!.categoryDao())
        ClassificationRepositoryProvider.movementHistoryRepository =
            RoomMovementHistoryRepository(database!!.movementDao())
    }

    fun getDatabase(): FinanzasDatabase = database!!
}
