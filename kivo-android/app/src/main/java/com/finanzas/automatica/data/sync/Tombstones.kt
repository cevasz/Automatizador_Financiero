package com.finanzas.automatica.data.sync

import android.util.Log
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.entity.SyncDeletionEntity

/**
 * Deja constancia de lo que se borra en el telefono, para que el borrado llegue
 * a la nube y a los demas dispositivos.
 *
 * Por que hace falta: la sincronizacion sube "todo lo que hay en Room". Una
 * fila borrada, sencillamente, ya no esta — no hay nada que subir que diga
 * "esto se fue". Sin lapida, el siguiente `pull` traeria la fila de vuelta
 * desde el servidor y el borrado se desharia solo. Con "Borrar todos mis datos"
 * el efecto seria aun peor: la app quedaria vacia y en la siguiente
 * sincronizacion se repoblaria entera.
 *
 * Hay que llamar a estos metodos **antes** del borrado: despues, el syncId ya
 * no se puede leer.
 *
 * Ninguna de estas funciones lanza. Fallar al registrar una lapida no puede
 * impedir que el usuario borre algo suyo.
 */
class Tombstones(private val db: FinanzasDatabase) {

    private val sync get() = db.syncDao()

    suspend fun antesDeBorrarMovimiento(id: Long) = registrar("movements", sync.syncIdMovement(id))
    suspend fun antesDeBorrarPresupuesto(id: Long) = registrar("budgets", sync.syncIdBudget(id))
    suspend fun antesDeBorrarMeta(id: Long) = registrar("savings_goals", sync.syncIdGoal(id))
    suspend fun antesDeBorrarContacto(id: Long) = registrar("agenda_entries", sync.syncIdAgenda(id))
    suspend fun antesDeBorrarCategoria(id: Long) = registrar("categories", sync.syncIdCategory(id))
    suspend fun antesDeBorrarRegla(id: Long) = registrar("classification_rules", sync.syncIdRule(id))

    /** Una factura arrastra sus productos por llave foranea en cascada. */
    suspend fun antesDeBorrarFactura(id: Long) {
        sync.syncIdsInvoiceItems(id).forEach { registrar("invoice_items", it) }
        registrar("invoices", sync.syncIdInvoice(id))
    }

    /**
     * Lapidas de absolutamente todo, para "Borrar todos mis datos" en Ajustes.
     * Es la unica forma de que ese boton signifique lo que dice: sin esto, los
     * datos volverian en la siguiente sincronizacion.
     */
    suspend fun antesDeBorrarTodo() {
        runCatching {
            sync.allInvoiceItems().forEach { registrar("invoice_items", it.syncId) }
            sync.allInvoices().forEach { registrar("invoices", it.syncId) }
            sync.allRules().forEach { registrar("classification_rules", it.syncId) }
            sync.allSavingsGoals().forEach { registrar("savings_goals", it.syncId) }
            sync.allBudgets().forEach { registrar("budgets", it.syncId) }
            sync.allMovements().forEach { registrar("movements", it.syncId) }
            sync.allAgendaEntries().forEach { registrar("agenda_entries", it.syncId) }
            sync.allCategories().forEach { registrar("categories", it.syncId) }
        }.onFailure { Log.w(TAG, "No se pudieron registrar todas las lápidas", it) }
    }

    private suspend fun registrar(tabla: String, syncId: String?) {
        if (syncId.isNullOrBlank()) return
        try {
            db.syncDeletionDao().insert(SyncDeletionEntity(syncId = syncId, tableName = tabla))
        } catch (e: Throwable) {
            Log.w(TAG, "No se pudo registrar la lápida de $tabla/$syncId", e)
        }
    }

    private companion object {
        const val TAG = "Tombstones"
    }
}
