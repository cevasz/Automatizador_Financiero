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
        ClassificationRuleEntity::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class,
        AppNotificationEntity::class
    ],
    version = 3,
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
    abstract fun invoiceDao(): InvoiceDao
    abstract fun appNotificationDao(): AppNotificationDao

    companion object {
        @Volatile
        private var INSTANCE: FinanzasDatabase? = null

        fun getInstance(context: Context): FinanzasDatabase {
            return INSTANCE ?: synchronized(this) {
                // Doble verificacion dentro del synchronized: sin esto, dos hilos que
                // entran a la vez al bloque crean dos instancias distintas del MISMO
                // archivo de base de datos (Room no lo impide), lo que puede dejar la
                // base en un estado inconsistente.
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): FinanzasDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                FinanzasDatabase::class.java,
                "finanzas.db"
            )
                .fallbackToDestructiveMigration()
                // Tambien al BAJAR de version (p.ej. si el usuario reinstala un APK
                // anterior): sin esto Room lanza IllegalStateException al abrir y la app
                // crashea en cada arranque, sin forma de recuperarse desde la app.
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
    }
}