package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.repository.AgendaRepositoryImpl
import com.finanzas.automatica.data.repository.CategoryRepositoryImpl
import com.finanzas.automatica.data.repository.InvoiceRepository
import com.finanzas.automatica.domain.enrichment.toDomain
import com.finanzas.automatica.domain.model.AgendaEntry
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.domain.model.DebtStatus
import com.finanzas.automatica.domain.model.DebtSummary
import com.finanzas.automatica.domain.model.Invoice
import com.finanzas.automatica.domain.model.InvoiceItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InvoiceViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val invoiceRepo = InvoiceRepository(database)
    private val agendaRepo = AgendaRepositoryImpl(database)
    private val categoryRepo = CategoryRepositoryImpl(database)

    val invoices: StateFlow<List<Invoice>> = invoiceRepo.getInvoicesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDebts: StateFlow<List<InvoiceItem>> = invoiceRepo.getAllDebtsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debtSummaries: StateFlow<List<DebtSummary>> = invoiceRepo.getDebtSummariesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _contacts = kotlinx.coroutines.flow.MutableStateFlow<List<AgendaEntry>>(emptyList())
    val contacts: StateFlow<List<AgendaEntry>> = _contacts

    private val _categories = kotlinx.coroutines.flow.MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    init {
        loadContactsAndCategories()
    }

    private fun loadContactsAndCategories() {
        viewModelScope.launch {
            try {
                _contacts.value = agendaRepo.findAll().map { it.toDomain() }
                _categories.value = categoryRepo.getAll().map { it.toDomain() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveInvoice(
        merchantName: String,
        totalAmount: Long,
        items: List<InvoiceItem>,
        imageUri: String? = null
    ) {
        viewModelScope.launch {
            try {
                invoiceRepo.saveInvoice(
                    merchantName = merchantName,
                    totalAmount = totalAmount,
                    items = items,
                    imageUri = imageUri
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markDebtAsPaid(itemId: Long) {
        viewModelScope.launch {
            try {
                invoiceRepo.updateDebtStatus(itemId, DebtStatus.PAID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markDebtAsPending(itemId: Long) {
        viewModelScope.launch {
            try {
                invoiceRepo.updateDebtStatus(itemId, DebtStatus.PENDING)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteInvoice(invoiceId: Long) {
        viewModelScope.launch {
            try {
                invoiceRepo.deleteInvoice(invoiceId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Genera una plantilla de productos simulados al subir o escanear una factura.
     */
    fun createSampleParsedInvoice(): Pair<String, List<InvoiceItem>> {
        val sampleItems = listOf(
            InvoiceItem(
                productName = "Hamburguesa Especial",
                quantity = 1,
                unitPrice = 2800000, // $28.000 COP
                totalPrice = 2800000,
                isDebt = false
            ),
            InvoiceItem(
                productName = "Papas en Casco",
                quantity = 1,
                unitPrice = 1200000, // $12.000 COP
                totalPrice = 1200000,
                isDebt = true,
                debtorName = "Juan Pérez",
                notes = "Papas compartidas pedir dinero"
            ),
            InvoiceItem(
                productName = "Cerveza Artesanal",
                quantity = 2,
                unitPrice = 1400000, // $14.000 x 2 = $28.000
                totalPrice = 2800000,
                isDebt = true,
                debtorName = "Carlos",
                notes = "Cervezas de Carlos"
            )
        )
        return Pair("Restaurante El Corral", sampleItems)
    }
}
