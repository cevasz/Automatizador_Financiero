package com.finanzas.automatica

import android.app.Application
import androidx.room.RoomDatabase
import androidx.room.Room
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.repository.DefaultCategories
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
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Poblar categorías por defecto
                    CoroutineScope(Dispatchers.IO).launch {
                        DefaultCategories.seed(database!!)
                    }
                }
            })
            .build()
    }

    fun getDatabase(): FinanzasDatabase = database!!
}