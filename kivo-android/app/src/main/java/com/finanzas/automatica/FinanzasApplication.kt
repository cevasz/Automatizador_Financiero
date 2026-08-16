package com.finanzas.automatica

import android.app.Application
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
        // Usa la MISMA instancia compartida que el resto de la app (MainActivity,
        // NotificationCaptureService, etc. llaman a FinanzasDatabase.getInstance()).
        // Antes esto construia una segunda instancia de Room apuntando al mismo archivo
        // "finanzas.db" -- dos instancias del mismo archivo pueden dejar la base en un
        // estado inconsistente, y ademas se saltaba la configuracion de migracion
        // centralizada (fallbackToDestructiveMigrationOnDowngrade).
        database = FinanzasDatabase.getInstance(applicationContext)

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
