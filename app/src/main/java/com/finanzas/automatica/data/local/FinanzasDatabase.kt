package com.finanzas.automatica.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.finanzas.automatica.data.local.converters.Converters
import com.finanzas.automatica.data.local.dao.*
import com.finanzas.automatica.data.local.entity.*

@Database(
    entities = [
        MovementEntity::class,
        AgendaEntryEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        SavingsGoalEntity::class,
        ClassificationRuleEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class FinanzasDatabase : RoomDatabase() {

    abstract fun movementDao(): MovementDao
    abstract fun agendaDao(): AgendaDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun classificationRuleDao(): ClassificationRuleDao

    companion object {
        @Volatile
        private var INSTANCE: FinanzasDatabase? = null

        fun getInstance(context: Context): FinanzasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinanzasDatabase::class.java,
                    "finanzas.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}