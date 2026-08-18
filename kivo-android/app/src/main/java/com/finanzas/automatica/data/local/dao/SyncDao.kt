package com.finanzas.automatica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.finanzas.automatica.data.local.entity.AgendaEntryEntity
import com.finanzas.automatica.data.local.entity.BudgetEntity
import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.data.local.entity.ClassificationRuleEntity
import com.finanzas.automatica.data.local.entity.InvoiceEntity
import com.finanzas.automatica.data.local.entity.InvoiceItemEntity
import com.finanzas.automatica.data.local.entity.MovementEntity
import com.finanzas.automatica.data.local.entity.SavingsGoalEntity

/**
 * Consultas que solo usa la sincronizacion: buscar por `syncId`, listar tablas
 * completas y borrar por `syncId`.
 *
 * Viven en su propio DAO en vez de repartidas por los ocho DAO existentes para
 * que el resto de la app siga sin saber que existe una nube — la misma razon
 * por la que el motor de parseo no sabe nada de la UI (ver CLAUDE.md).
 */
@Dao
interface SyncDao {

    // --- Lecturas completas (lo que se sube) ---------------------------------

    @Query("SELECT * FROM categories") suspend fun allCategories(): List<CategoryEntity>
    @Query("SELECT * FROM agenda_entries") suspend fun allAgendaEntries(): List<AgendaEntryEntity>
    @Query("SELECT * FROM movements") suspend fun allMovements(): List<MovementEntity>
    @Query("SELECT * FROM budgets") suspend fun allBudgets(): List<BudgetEntity>
    @Query("SELECT * FROM savings_goals") suspend fun allSavingsGoals(): List<SavingsGoalEntity>
    @Query("SELECT * FROM classification_rules") suspend fun allRules(): List<ClassificationRuleEntity>
    @Query("SELECT * FROM invoices") suspend fun allInvoices(): List<InvoiceEntity>
    @Query("SELECT * FROM invoice_items") suspend fun allInvoiceItems(): List<InvoiceItemEntity>

    // --- Busqueda por identidad de sincronizacion ----------------------------

    @Query("SELECT * FROM categories WHERE syncId = :syncId LIMIT 1")
    suspend fun categoryBySyncId(syncId: String): CategoryEntity?

    @Query("SELECT * FROM agenda_entries WHERE syncId = :syncId LIMIT 1")
    suspend fun agendaBySyncId(syncId: String): AgendaEntryEntity?

    @Query("SELECT * FROM movements WHERE syncId = :syncId LIMIT 1")
    suspend fun movementBySyncId(syncId: String): MovementEntity?

    @Query("SELECT * FROM budgets WHERE syncId = :syncId LIMIT 1")
    suspend fun budgetBySyncId(syncId: String): BudgetEntity?

    @Query("SELECT * FROM savings_goals WHERE syncId = :syncId LIMIT 1")
    suspend fun goalBySyncId(syncId: String): SavingsGoalEntity?

    @Query("SELECT * FROM classification_rules WHERE syncId = :syncId LIMIT 1")
    suspend fun ruleBySyncId(syncId: String): ClassificationRuleEntity?

    @Query("SELECT * FROM invoices WHERE syncId = :syncId LIMIT 1")
    suspend fun invoiceBySyncId(syncId: String): InvoiceEntity?

    @Query("SELECT * FROM invoice_items WHERE syncId = :syncId LIMIT 1")
    suspend fun invoiceItemBySyncId(syncId: String): InvoiceItemEntity?

    // --- Reconciliacion por llave natural ------------------------------------
    //
    // Cuando un dispositivo nuevo baja datos que el otro ya subio, la fila puede
    // existir localmente con OTRO syncId: las 33 categorias por defecto las
    // siembra cada telefono por su cuenta, con UUID propios. Sin estas
    // consultas, la sincronizacion insertaria una segunda "Alimentación" y
    // ademas chocaria con los indices unicos de agenda y presupuestos.

    @Query("SELECT * FROM categories WHERE name = :name AND type = :type LIMIT 1")
    suspend fun categoryByNaturalKey(name: String, type: String): CategoryEntity?

    @Query("SELECT * FROM agenda_entries WHERE accountIdentifier = :identifier LIMIT 1")
    suspend fun agendaByNaturalKey(identifier: String): AgendaEntryEntity?

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND month = :month AND year = :year LIMIT 1")
    suspend fun budgetByNaturalKey(categoryId: Long, month: Int, year: Int): BudgetEntity?

    // --- Escrituras ----------------------------------------------------------

    @Insert suspend fun insertCategory(entity: CategoryEntity): Long
    @Insert suspend fun insertAgenda(entity: AgendaEntryEntity): Long
    @Insert suspend fun insertMovement(entity: MovementEntity): Long
    @Insert suspend fun insertBudget(entity: BudgetEntity): Long
    @Insert suspend fun insertGoal(entity: SavingsGoalEntity): Long
    @Insert suspend fun insertRule(entity: ClassificationRuleEntity): Long
    @Insert suspend fun insertInvoice(entity: InvoiceEntity): Long
    @Insert suspend fun insertInvoiceItem(entity: InvoiceItemEntity): Long

    @Update suspend fun updateCategory(entity: CategoryEntity): Int
    @Update suspend fun updateAgenda(entity: AgendaEntryEntity): Int
    @Update suspend fun updateMovement(entity: MovementEntity): Int
    @Update suspend fun updateBudget(entity: BudgetEntity): Int
    @Update suspend fun updateGoal(entity: SavingsGoalEntity): Int
    @Update suspend fun updateRule(entity: ClassificationRuleEntity): Int
    @Update suspend fun updateInvoice(entity: InvoiceEntity): Int
    @Update suspend fun updateInvoiceItem(entity: InvoiceItemEntity): Int

    // --- syncId de una fila local, para dejar su lapida al borrarla ----------

    @Query("SELECT syncId FROM categories WHERE id = :id") suspend fun syncIdCategory(id: Long): String?
    @Query("SELECT syncId FROM agenda_entries WHERE id = :id") suspend fun syncIdAgenda(id: Long): String?
    @Query("SELECT syncId FROM movements WHERE id = :id") suspend fun syncIdMovement(id: Long): String?
    @Query("SELECT syncId FROM budgets WHERE id = :id") suspend fun syncIdBudget(id: Long): String?
    @Query("SELECT syncId FROM savings_goals WHERE id = :id") suspend fun syncIdGoal(id: Long): String?
    @Query("SELECT syncId FROM classification_rules WHERE id = :id") suspend fun syncIdRule(id: Long): String?
    @Query("SELECT syncId FROM invoices WHERE id = :id") suspend fun syncIdInvoice(id: Long): String?
    @Query("SELECT syncId FROM invoice_items WHERE invoiceId = :invoiceId") suspend fun syncIdsInvoiceItems(invoiceId: Long): List<String>

    // --- Borrado propagado desde la nube -------------------------------------

    @Query("DELETE FROM categories WHERE syncId = :syncId") suspend fun deleteCategory(syncId: String): Int
    @Query("DELETE FROM agenda_entries WHERE syncId = :syncId") suspend fun deleteAgenda(syncId: String): Int
    @Query("DELETE FROM movements WHERE syncId = :syncId") suspend fun deleteMovement(syncId: String): Int
    @Query("DELETE FROM budgets WHERE syncId = :syncId") suspend fun deleteBudget(syncId: String): Int
    @Query("DELETE FROM savings_goals WHERE syncId = :syncId") suspend fun deleteGoal(syncId: String): Int
    @Query("DELETE FROM classification_rules WHERE syncId = :syncId") suspend fun deleteRule(syncId: String): Int
    @Query("DELETE FROM invoices WHERE syncId = :syncId") suspend fun deleteInvoice(syncId: String): Int
    @Query("DELETE FROM invoice_items WHERE syncId = :syncId") suspend fun deleteInvoiceItem(syncId: String): Int
}
