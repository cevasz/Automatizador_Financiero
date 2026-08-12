package com.finanzas.automatica.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.finanzas.automatica.data.local.entity.InvoiceEntity
import com.finanzas.automatica.data.local.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

data class InvoiceWithItemsRelation(
    @Embedded val invoice: InvoiceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val items: List<InvoiceItemEntity>
)

@Dao
interface InvoiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceItems(items: List<InvoiceItemEntity>): List<Long>

    @Transaction
    @Query("SELECT * FROM invoices ORDER BY date DESC")
    fun getInvoicesWithItemsFlow(): Flow<List<InvoiceWithItemsRelation>>

    @Query("SELECT * FROM invoice_items WHERE isDebt = 1 ORDER BY createdAt DESC")
    fun getAllDebtsFlow(): Flow<List<InvoiceItemEntity>>

    @Query("UPDATE invoice_items SET debtStatus = :status WHERE id = :itemId")
    suspend fun updateDebtStatus(itemId: Long, status: String)

    @Query("DELETE FROM invoices WHERE id = :invoiceId")
    suspend fun deleteInvoice(invoiceId: Long)
}
