package com.finanzas.automatica.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.sync.Tombstones
import com.finanzas.automatica.data.repository.AgendaRepositoryImpl
import com.finanzas.automatica.data.repository.CategoryRepositoryImpl
import com.finanzas.automatica.data.repository.InvoiceRepository
import com.finanzas.automatica.domain.enrichment.toDomain
import com.finanzas.automatica.domain.enrichment.toDomainSafely
import com.finanzas.automatica.domain.importer.ImageTextRecognizer
import com.finanzas.automatica.domain.importer.ReceiptOcrParser
import com.finanzas.automatica.domain.model.AgendaEntry
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.domain.model.DebtStatus
import com.finanzas.automatica.domain.model.DebtSummary
import com.finanzas.automatica.domain.model.Invoice
import com.finanzas.automatica.domain.model.InvoiceItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InvoiceViewModel(
    private val database: FinanzasDatabase,
    private val appContext: Context
) : ViewModel() {

    private val invoiceRepo = InvoiceRepository(database)
    private val agendaRepo = AgendaRepositoryImpl(database)
    private val categoryRepo = CategoryRepositoryImpl(database)
    private val tombstones = Tombstones(database)

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
                _contacts.value = agendaRepo.findAll().toDomainSafely { it.toDomain() }
                _categories.value = categoryRepo.getAll().toDomainSafely { it.toDomain() }
            } catch (t: Throwable) {
                t.printStackTrace()
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
                // Antes del borrado, y tambien por los productos: la factura
                // los arrastra en cascada, asi que hay que dejar su lapida
                // mientras todavia existen.
                tombstones.antesDeBorrarFactura(invoiceId)
                invoiceRepo.deleteInvoice(invoiceId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * OCR real (ML Kit, 100% local) sobre una foto tomada con la camara del sistema.
     * Devuelve el comercio detectado + los productos que se pudieron reconocer para que
     * el usuario los revise/edite antes de guardar -- el reconocimiento de un recibo
     * fotografiado nunca es perfecto, por eso el borrador siempre queda editable.
     */
    suspend fun scanReceiptBitmap(bitmap: Bitmap): Pair<String, List<InvoiceItem>> {
        return withContext(Dispatchers.Default) {
            val text = ImageTextRecognizer.recognize(bitmap)
            ReceiptOcrParser.parse(text)
        }
    }

    /** Igual que [scanReceiptBitmap] pero para una imagen elegida de la galeria. */
    suspend fun scanReceiptUri(uri: Uri): Pair<String, List<InvoiceItem>> {
        val text = ImageTextRecognizer.recognize(appContext, uri)
        return withContext(Dispatchers.Default) {
            ReceiptOcrParser.parse(text)
        }
    }

    /**
     * Plantilla de ejemplo para probar el flujo sin tener una factura a la mano (queda
     * como accion secundaria en el estado vacio, ya no es el boton principal de "escanear").
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
