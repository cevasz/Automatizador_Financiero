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

        // Se ejecuta en CADA arranque de la app, sin manejador de excepciones propio --
        // si seed()/dedupe() llegaran a lanzar algo no previsto (p.ej. un estado de
        // datos inesperado en un dispositivo real), sin este try/catch la excepcion
        // tumba el proceso completo y la app queda sin poder abrirse nunca mas (crashea
        // en cada intento de abrir, porque esto corre antes que cualquier pantalla).
        CoroutineScope(Dispatchers.IO).launch {
            try {
                DefaultCategories.seed(database!!)
                // Limpia categorias duplicadas dejadas por versiones anteriores (seed()
                // se ejecutaba en cada arranque sin verificar si ya existian). Idempotente.
                DefaultCategories.dedupe(database!!)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }

        ClassificationRepositoryProvider.categoryLookupRepository =
            RoomCategoryLookupRepository(database!!.categoryDao())
        ClassificationRepositoryProvider.movementHistoryRepository =
            RoomMovementHistoryRepository(database!!.movementDao())
    }

    fun getDatabase(): FinanzasDatabase = database!!
}
