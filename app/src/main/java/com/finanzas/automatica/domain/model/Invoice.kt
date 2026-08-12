package com.finanzas.automatica.domain.model

import java.time.Instant

data class Invoice(
    val id: Long = 0,
    val merchantName: String,
    val date: Instant = Instant.now(),
    val totalAmount: Long, // Centavos COP
    val imageUri: String? = null,
    val movementId: Long? = null,
    val items: List<InvoiceItem> = emptyList(),
    val createdAt: Instant = Instant.now()
)

data class InvoiceItem(
    val id: Long = 0,
    val invoiceId: Long = 0,
    val productName: String,
    val quantity: Int = 1,
    val unitPrice: Long, // Centavos COP
    val totalPrice: Long, // Centavos COP
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val isDebt: Boolean = false,
    val debtorContactId: Long? = null,
    val debtorName: String? = null,
    val debtStatus: DebtStatus = DebtStatus.PENDING,
    val notes: String? = null
)

enum class DebtStatus {
    PENDING, PAID
}

data class DebtSummary(
    val debtorName: String,
    val debtorContactId: Long?,
    val totalOwed: Long, // Centavos COP
    val itemsCount: Int,
    val pendingItems: List<InvoiceItem>
)
