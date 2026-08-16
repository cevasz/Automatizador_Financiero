package com.finanzas.automatica.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.entity.AppNotificationEntity
import com.finanzas.automatica.data.repository.AppNotificationRepository
import com.finanzas.automatica.data.repository.CategoryRepositoryImpl
import com.finanzas.automatica.data.repository.MovementRepositoryImpl
import com.finanzas.automatica.domain.enrichment.EnrichmentPipeline
import com.finanzas.automatica.domain.enrichment.toDomain
import com.finanzas.automatica.domain.importer.ImageTextRecognizer
import com.finanzas.automatica.domain.importer.ImportSummary
import com.finanzas.automatica.domain.importer.PdfStatementExtractor
import com.finanzas.automatica.domain.importer.StatementImporter
import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.domain.model.ConfirmationState
import com.finanzas.automatica.domain.model.Movement
import com.finanzas.automatica.domain.model.MovementSource
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.RawMovement
import com.finanzas.automatica.domain.parser.ParserRegistry
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovementViewModel(
    private val database: FinanzasDatabase,
    private val appContext: Context
) : ViewModel() {

    private val repository = MovementRepositoryImpl(database)
    private val categoryRepository = CategoryRepositoryImpl(database)
    private val notifications = AppNotificationRepository(database)
    private val parserRegistry = ParserRegistry.createDefault()

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

    /**
     * Los extractos bancarios colombianos casi siempre vienen protegidos con
     * contraseña. Si [password] es nula/incorrecta y el PDF la requiere,
     * [onPasswordError] se dispara para que la UI pida la contraseña (o reintente) en
     * vez de reportarse como una falla generica de importacion.
     *
     * Atrapa `Throwable`, no solo `Exception`: el descifrado real (no solo detectar que
     * hace falta contraseña) pasa por BouncyCastle via pdfbox-android, y un extracto
     * real con un algoritmo/version de cifrado que la libreria no maneja del todo bien
     * puede lanzar un `Error` (no una `Exception`) -- eso antes escapaba de este bloque
     * y tumbaba toda la app (bug reportado: "al ingresar la contraseña se cierra la app
     * y se reinicia"). Nada de lo que pase aca debe crashear el proceso.
     */
    fun importStatementPdf(
        pdfBytes: ByteArray,
        defaultBank: BankEntity,
        password: String? = null,
        onComplete: (ImportSummary) -> Unit,
        onPasswordError: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val text = withContext(Dispatchers.Default) {
                    PdfStatementExtractor.extractText(pdfBytes, password)
                }
                importRaw(text, defaultBank, onComplete)
            } catch (e: InvalidPasswordException) {
                _isLoading.value = false
                onPasswordError()
            } catch (t: Throwable) {
                t.printStackTrace()
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
        val summary = StatementImporter.parseStatementText(text, defaultBank)
        importMovements(summary.importedMovements, onComplete)
    }

    /**
     * Escanea una captura de pantalla (galeria) para detectar un movimiento que no llegó
     * como notificacion -- app fuera del listener, notificacion ya descartada, confirmacion
     * en pantalla completa de la app del banco, etc. Reusa exactamente el mismo motor de
     * reglas/regex que procesa notificaciones reales (ParserRegistry): si el texto
     * reconocido por OCR menciona el banco y un monto, se procesa igual que una
     * notificacion. Si ningun banco coincide, cae a StatementImporter (funciona con
     * capturas de un historial/extracto en pantalla con fecha + monto por linea).
     */
    fun importScreenshot(uri: Uri, onComplete: (ImportSummary) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val text = try {
                withContext(Dispatchers.IO) { ImageTextRecognizer.recognize(appContext, uri) }
            } catch (t: Throwable) {
                t.printStackTrace()
                _isLoading.value = false
                onComplete(ImportSummary(0, 0, 0, 0, 0, emptyList()))
                return@launch
            }

            val bankMatch = parserRegistry.parse("", text)
            val movements = if (bankMatch is ParseResult.Success) {
                listOf(bankMatch.movement.copy(source = MovementSource.OCR))
            } else {
                StatementImporter.parseStatementText(text, BankEntity.UNKNOWN, MovementSource.OCR)
                    .importedMovements
            }
            importMovements(movements, onComplete)
        }
    }

    private suspend fun importMovements(
        movements: List<RawMovement>,
        onComplete: (ImportSummary) -> Unit
    ) {
        _isLoading.value = true
        try {
            val pipeline = EnrichmentPipeline(database)
            for (movement in movements) {
                pipeline.process(movement)
            }

            val incomeList = movements.filter { it.type == MovementType.INCOME }
            val expenseList = movements.filter { it.type == MovementType.EXPENSE }
            val summary = ImportSummary(
                totalCount = movements.size,
                incomeCount = incomeList.size,
                expenseCount = expenseList.size,
                totalIncomeAmount = incomeList.sumOf { it.amount },
                totalExpenseAmount = expenseList.sumOf { it.amount },
                importedMovements = movements
            )

            if (summary.totalCount > 0) {
                notifications.notify(
                    type = AppNotificationEntity.TYPE_MOVEMENTS,
                    title = "Movimientos capturados",
                    message = "${summary.totalCount} movimientos registrados: ${summary.incomeCount} ingresos y ${summary.expenseCount} egresos."
                )
            }

            onComplete(summary)
        } catch (t: Throwable) {
            // Throwable, no solo Exception: es el punto final compartido por los tres
            // flujos de importacion (texto pegado, PDF, captura de pantalla) -- ninguno
            // debe poder crashear la app por lo que pase leyendo datos del usuario.
            t.printStackTrace()
            onComplete(ImportSummary(0, 0, 0, 0, 0, emptyList()))
        } finally {
            _isLoading.value = false
        }
    }
}
