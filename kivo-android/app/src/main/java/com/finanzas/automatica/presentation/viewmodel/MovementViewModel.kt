package com.finanzas.automatica.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.entity.AppNotificationEntity
import com.finanzas.automatica.data.repository.AppNotificationRepository
import com.finanzas.automatica.data.repository.CategoryRepositoryImpl
import com.finanzas.automatica.data.repository.MovementRepositoryImpl
import com.finanzas.automatica.domain.enrichment.toDomain
import com.finanzas.automatica.domain.importer.ImportSummary
import com.finanzas.automatica.domain.importer.PdfStatementExtractor
import com.finanzas.automatica.domain.importer.StatementImporter
import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.domain.model.ConfirmationState
import com.finanzas.automatica.domain.model.Movement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovementViewModel(
    private val database: FinanzasDatabase
) : ViewModel() {

    private val repository = MovementRepositoryImpl(database)
    private val categoryRepository = CategoryRepositoryImpl(database)
    private val notifications = AppNotificationRepository(database)

    // Reactivo sobre Room: se usa para el dialogo de recategorizar un movimiento
    // (boton "Detalle" en MovementsListScreen), que antes no tenia ningun efecto.
    val categories: StateFlow<List<Category>> = categoryRepository.getAllFlow()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reactivo sobre Room: cualquier escritura a la tabla "movements" (desde esta
    // instancia o desde cualquier otra, p. ej. Dashboard y la lista de Movimientos
    // observan cada una su propio MovementViewModel) reemite automáticamente, así
    // que confirmar/rechazar un movimiento se refleja al instante en toda la app sin
    // necesidad de refrescar manualmente ni de reiniciar la app.
    val movements: StateFlow<List<Movement>> = repository.getAllFlow()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingCount: StateFlow<Int> = movements
        .map { list -> list.count { it.confirmationState == ConfirmationState.PENDING } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun confirmMovement(id: Long) {
        viewModelScope.launch {
            repository.updateConfirmationState(id, "CONFIRMED")
        }
    }

    fun rejectMovement(id: Long) {
        viewModelScope.launch {
            repository.updateConfirmationState(id, "REJECTED")
        }
    }

    fun correctMovement(id: Long, newCategoryId: Long) {
        viewModelScope.launch {
            repository.updateCategory(id, newCategoryId)
            repository.updateConfirmationState(id, "CORRECTED")
        }
    }

    fun importStatementText(
        text: String,
        defaultBank: BankEntity,
        onComplete: (ImportSummary) -> Unit
    ) {
        viewModelScope.launch {
            importRaw(text, defaultBank, onComplete)
        }
    }

    fun importStatementPdf(
        pdfBytes: ByteArray,
        defaultBank: BankEntity,
        onComplete: (ImportSummary) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val text = withContext(Dispatchers.Default) {
                    PdfStatementExtractor.extractText(pdfBytes)
                }
                importRaw(text, defaultBank, onComplete)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(ImportSummary(0, 0, 0, 0, 0, emptyList()))
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun importRaw(
        text: String,
        defaultBank: BankEntity,
        onComplete: (ImportSummary) -> Unit
    ) {
        _isLoading.value = true
        try {
            val summary = StatementImporter.parseStatementText(text, defaultBank)
            val pipeline = com.finanzas.automatica.domain.enrichment.EnrichmentPipeline(database)

            for (movement in summary.importedMovements) {
                pipeline.process(movement)
            }

            val total = summary.importedMovements.size
            if (total > 0) {
                notifications.notify(
                    type = AppNotificationEntity.TYPE_MOVEMENTS,
                    title = "Movimientos capturados",
                    message = "$total movimientos registrados: ${summary.incomeCount} ingresos y ${summary.expenseCount} egresos."
                )
            }

            onComplete(summary)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isLoading.value = false
        }
    }
}
