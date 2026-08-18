package com.finanzas.automatica.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migraciones explicitas de Room.
 *
 * Antes de la version 4 la base se apoyaba solo en
 * `fallbackToDestructiveMigration()`, que ante un cambio de esquema **borra
 * todos los datos del usuario**. Eso era tolerable mientras la app no habia
 * salido de pruebas; con movimientos reales capturados durante meses, no.
 * El fallback destructivo se conserva como ultimo recurso (mejor perder datos
 * que dejar la app sin abrir, ver docs/PENDIENTES.md), pero cada version nueva
 * debe traer su Migration.
 */
object FinanzasMigrations {

    /** Tablas que se sincronizan con la nube y por tanto necesitan `syncId`. */
    private val TABLAS_SINCRONIZADAS = listOf(
        "movements",
        "agenda_entries",
        "categories",
        "budgets",
        "savings_goals",
        "classification_rules",
        "invoices",
        "invoice_items"
    )

    /**
     * Expresion SQLite que genera un UUID v4 con el formato canonico
     * (8-4-4-4-12, con el `4` de version y uno de `8/9/a/b` como variante).
     *
     * Se hace en SQL y no en Kotlin porque hay que rellenar filas ya
     * existentes: leerlas todas a memoria, generar los UUID y reescribirlas
     * seria mucho mas lento y mas fragil justo en el arranque de la app, que es
     * donde corre una migracion.
     */
    private const val UUID_SQL = """
        lower(
          hex(randomblob(4)) || '-' ||
          hex(randomblob(2)) || '-4' ||
          substr(hex(randomblob(2)), 2) || '-' ||
          substr('89ab', abs(random()) % 4 + 1, 1) ||
          substr(hex(randomblob(2)), 2) || '-' ||
          hex(randomblob(6))
        )
    """

    /**
     * v3 → v4: identidad de sincronizacion.
     *
     * - `syncId` en cada tabla sincronizable, con UUID generado para las filas
     *   que ya existen en el dispositivo.
     * - Tabla `sync_deletions` para las lapidas de borrado.
     */
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            TABLAS_SINCRONIZADAS.forEach { tabla ->
                // El DEFAULT '' es obligatorio: SQLite no permite agregar una
                // columna NOT NULL sin valor por defecto a una tabla con filas.
                // Por eso la entidad declara @ColumnInfo(defaultValue = "") —
                // si no coincidiera, Room fallaria la validacion al abrir.
                db.execSQL("ALTER TABLE $tabla ADD COLUMN syncId TEXT NOT NULL DEFAULT ''")
                db.execSQL("UPDATE $tabla SET syncId = $UUID_SQL WHERE syncId = ''")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_${tabla}_syncId ON $tabla (syncId)")
            }

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS sync_deletions (
                    syncId TEXT NOT NULL,
                    tableName TEXT NOT NULL,
                    deletedAt INTEGER NOT NULL,
                    PRIMARY KEY(syncId)
                )
                """.trimIndent()
            )
        }
    }

    /**
     * v4 → v5: repara estados de confirmacion invalidos.
     *
     * `MovementViewModel.correctMovement()` escribia el estado `"CORRECTED"`, que no
     * existe en el enum `ConfirmationState`. Consecuencias reales de cada fila asi:
     *
     *  1. `ConfirmationState.valueOf("CORRECTED")` lanza al releerla, y
     *     `toDomainSafely` la descarta — el movimiento **desaparecia de la lista**,
     *     y de forma permanente, porque el valor malo quedaba guardado.
     *  2. Desde que existe la sincronizacion, la columna equivalente en Postgres
     *     tiene un CHECK con los cuatro valores validos: una sola fila con
     *     "CORRECTED" habria hecho fallar el push **entero**, no solo esa fila.
     *
     * Se normaliza a CONFIRMED porque es lo que significaba: alguien reviso ese
     * movimiento y le corrigio la categoria. El `WHERE ... NOT IN` cubre tambien
     * cualquier otro valor invalido que hubiera podido colarse.
     */
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                UPDATE movements
                   SET confirmationState = 'CONFIRMED'
                 WHERE confirmationState NOT IN ('PENDING', 'CONFIRMED', 'REJECTED', 'AUTO_CONFIRMED')
                """.trimIndent()
            )
        }
    }

    val TODAS = arrayOf(MIGRATION_3_4, MIGRATION_4_5)
}
