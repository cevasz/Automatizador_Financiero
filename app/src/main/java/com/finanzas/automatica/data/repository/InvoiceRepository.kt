package com.finanzas.automatica.data.repository

import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.entity.InvoiceEntity
import com.finanzas.automatica.data.local.entity.InvoiceItemEntity
import com.finanzas.automatica.domain.model.DebtStatus
import com.finanzas.automatica.domain.model.DebtSummary
import com.finanzas.automatica.domain.model.Invoice
import com.finanzas.automatica.domain.model.InvoiceItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class InvoiceRepository(
    private val database: FinanzasDatabase
) {
    private val dao = database.invoiceDao()

    fun getInvoicesFlow(): Flow<List<Invoice>> {
        return dao.getInvoicesWithItemsFlow().map { list ->
            list.map { relation ->
                Invoice(
                    id = relation.invoice.id,
                    merchantName = relation.invoice.merchantName,
                    date = Instant.ofEpochMilli(relation.invoice.date),
                    totalAmount = relation.invoice.totalAmount,
                    imageUri = relation.invoice.imageUri,
                    movementId = relation.invoice.movementId,
                    items = relation.items.map { item ->
                        InvoiceItem(
                            id = item.id,
                            invoiceId = item.invoiceId,
                            productName = item.productName,
                            quantity = item.quantity,
                            unitPrice = item.unitPrice,
                            totalPrice = item.totalPrice,
                            categoryId = item.categoryId,
                            isDebt = item.isDebt,
                            debtorContactId = item.debtorContactId,
                            debtorName = item.debtorName,
                            debtStatus = if (item.debtStatus == "PAID") DebtStatus.PAID else DebtStatus.PENDING,
                            notes = item.notes
                        )
                    },
                    createdAt = Instant.ofEpochMilli(relation.invoice.createdAt)
                )
            }
        }
    }

    fun getAllDebtsFlow(): Flow<List<InvoiceItem>> {
        return dao.getAllDebtsFlow().map { items ->
            items.map { item ->
                InvoiceItem(
                    id = item.id,
                    invoiceId = item.invoiceId,
                    productName = item.productName,
                    quantity = item.quantity,
                    unitPrice = item.unitPrice,
                    totalPrice = item.totalPrice,
                    categoryId = item.categoryId,
                    isDebt = item.isDebt,
                    debtorContactId = item.debtorContactId,
                    debtorName = item.debtorName,
                    debtStatus = if (item.debtStatus == "PAID") DebtStatus.PAID else DebtStatus.PENDING,
                    notes = item.notes
                )
            }
        }
    }

    fun getDebtSummariesFlow(): Flow<List<DebtSummary>> {
        return getAllDebtsFlow().map { items ->
            val pendingItems = items.filter { it.debtStatus == DebtStatus.PENDING && !it.debtorName.isNullBland() }
            pendingItems
                .groupBy { it.debtorName ?: "Sin Nombre" }
                .map { (debtorName, itemList) ->
                    DebtSummary(
                        debtorName = debtorName,
                        debtorContactId = itemList.firstOrNull()?.debtorContactId,
                        totalOwed = itemList.sumOf { it.totalPrice },
                        itemsCount = itemList.size,
                        pendingItems = itemList
                    )
                }
                .sortedByDescending { it.totalOwed }
        }
    }

    suspend fun saveInvoice(
        merchantName: String,
        totalAmount: Long,
        items: List<InvoiceItem>,
        imageUri: String? = null,
        movementId: Long? = null
    ): Long {
        val invoiceId = dao.insertInvoice(
            InvoiceEntity(
                merchantName = merchantName,
                totalAmount = totalAmount,
                imageUri = imageUri,
                movementId = movementId,
                date = Instant.now().toEpochMilli()
            )
        )

        val itemEntities = items.map { item ->
            InvoiceItemEntity(
                invoiceId = invoiceId,
                productName = item.productName,
                quantity = item.quantity,
                unitPrice = item.unitPrice,
                totalPrice = item.totalPrice,
                categoryId = item.categoryId,
                isDebt = item.isDebt,
                debtorContactId = item.debtorContactId,
                debtorName = item.debtorName,
                debtStatus = item.debtStatus.name,
                notes = item.notes
            )
        }
        dao.insertInvoiceItems(itemEntities)
        return invoiceId
    }

    suspend fun updateDebtStatus(itemId: Long, status: DebtStatus) {
        dao.updateDebtStatus(itemId, status.name)
    }

    suspend fun deleteInvoice(invoiceId: Long) {
        dao.deleteInvoice(invoiceId)
    }

    private fun String?.isNullBland(): Boolean {
        return this == null || this.trim().isEmpty()
    }
}
