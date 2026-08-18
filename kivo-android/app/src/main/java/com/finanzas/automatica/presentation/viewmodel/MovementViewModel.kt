package com.finanzas.automatica.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.data.local.entity.MovementEntity
import com.finanzas.automatica.data.sync.Tombstones
import com.finanzas.automatica.domain.model.PaymentMethod
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.entity.AppNotificationEntity
import com.finanzas.automatica.data.repository.AppNotificationRepository
import com.finanzas.automatica.data.repository.CategoryRepositoryImpl
import com.finanzas.automatica.data.repository.MovementRepositoryImpl
import com.finanzas.automatica.domain.enrichment.EnrichmentPipeline
import com.finanzas.automatica.domain.enrichment.toDomain
import com.finanzas.automatica.domain.enrichment.toDomainSafely
import com.finanzas.automatica.domain.importer.ImageTextRecognizer
import com.finanzas.automatica.domain.importer.ImportSummary
import com.finanzas.automatica.domain.importer.PdfStatementExtractor
import com.finanzas.automatica.domain.importer.StatementImporter
import com.finanzas.automatica.domain.model.BalanceAdjustment
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
        .map { entities -> entities.toDomainSafely { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reactivo sobre Room: cualquier escritura a la tabla "movements" (desde esta
    // instancia o desde cualquier otra, p. ej. Dashboard y la lista de Movimientos
    // observan cada una su propio MovementViewModel) reemite automáticamente, así
    // que confirmar/rechazar un movimiento se refleja al instante en toda la app sin
    // necesidad de refrescar manualmente ni de reiniciar la app.
    val movements: StateFlow<List<Movement>> = repository.getAllFlow()
        .map { entities -> entities.toDomainSafely { it.toDomain() } }
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

    /**
     * Recategoriza un movimiento y lo da por confirmado — corregir la categoria es,
     * en la practica, revisarlo y aceptarlo.
     *
     * **Bug corregido (2026-08-18)**: antes escribia el estado `"CORRECTED"`, que
     * **no existe** en [ConfirmationState]. Al releer la fila,
     * `ConfirmationState.valueOf("CORRECTED")` lanzaba y `toDomainSafely` descartaba
     * ese movimiento: recategorizar algo lo hacia **desaparecer de la lista para
     * siempre**, porque el valor invalido quedaba guardado en la base. Ademas, desde
     * que existe la sincronizacion, Postgres tiene un CHECK sobre esa columna, asi
     * que una sola fila con "CORRECTED" habria hecho fallar el push entero.
     */
    fun correctMovement(id: Long, newCategoryId: Long) {
        viewModelScope.launch {
            repository.updateCategory(id, newCategoryId)
            repository.updateConfirmationState(id, ConfirmationState.CONFIRMED.name)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Manejo manual del dinero
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Saldo neto segun Kivo: todo lo que entro menos todo lo que salio, contando
     * solo lo confirmado.
     *
     * Los movimientos por confirmar quedan fuera a proposito: mientras no se
     * revisan, no se sabe si son reales. Los rechazados tambien, obviamente.
     */
    val netBalance: StateFlow<Long> = movements
        .map { lista ->
            lista.filter {
                it.confirmationState == ConfirmationState.CONFIRMED ||
                    it.confirmationState == ConfirmationState.AUTO_CONFIRMED
            }.sumOf { if (it.type == MovementType.INCOME) it.amount else -it.amount }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    /** Registra un movimiento a mano. Nace confirmado: lo escribio el usuario. */
    fun createMovement(
        type: MovementType,
        amountCents: Long,
        counterparty: String,
        categoryId: Long?,
        dateMillis: Long,
        bank: BankEntity = BankEntity.UNKNOWN,
        note: String = "",
        onDone: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val ok = try {
                require(amountCents > 0) { "El monto debe ser mayor que cero" }
                repository.insert(
                    MovementEntity(
                        type = type.name,
                        amount = amountCents,
                        paymentMethod = PaymentMethod.OTHER.name,
                        counterpartyRaw = counterparty.trim(),
                        categoryId = categoryId,
                        date = dateMillis,
                        source = MovementSource.MANUAL.name,
                        confirmationState = ConfirmationState.CONFIRMED.name,
                        bankEntity = bank.name,
                        rawText = note
                    )
                )
                true
            } catch (e: Throwable) {
                Log.e(TAG, "No se pudo registrar el movimiento manual", e)
                false
            }
            onDone(ok)
        }
    }

    /**
     * Edita un movimiento ya registrado.
     *
     * Se conservan `createdAt`, `source` y `rawText`: son el rastro de **como
     * llego** ese movimiento. Si al corregir un monto se borrara el texto original
     * de la notificacion, se perderia la unica forma de comprobar despues por que
     * Kivo registro esa cifra.
     */
    fun updateMovement(
        id: Long,
        type: MovementType,
        amountCents: Long,
        counterparty: String,
        categoryId: Long?,
        dateMillis: Long,
        onDone: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val ok = try {
                require(amountCents > 0) { "El monto debe ser mayor que cero" }
                val actual = repository.getById(id) ?: error("El movimiento ya no existe")
                repository.update(
                    actual.copy(
                        type = type.name,
                        amount = amountCents,
                        counterpartyRaw = counterparty.trim(),
                        categoryId = categoryId,
                        date = dateMillis,
                        confirmationState = ConfirmationState.CONFIRMED.name,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                true
            } catch (e: Throwable) {
                Log.e(TAG, "No se pudo editar el movimiento $id", e)
                false
            }
            onDone(ok)
        }
    }

    /** Borra un movimiento. La lapida va antes: ver [Tombstones]. */
    fun deleteMovement(id: Long, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val ok = try {
                Tombstones(database).antesDeBorrarMovimiento(id)
                repository.deleteById(id) > 0
            } catch (e: Throwable) {
                Log.e(TAG, "No se pudo borrar el movimiento $id", e)
                false
            }
            onDone(ok)
        }
    }

    /**
     * "Cuadrar saldo": el usuario dice cuanto tiene de verdad y Kivo registra la
     * diferencia como un movimiento de ajuste.
     *
     * **Por que un movimiento y no un numero escondido.** La tentacion es guardar
     * un "saldo inicial" aparte y sumarlo al total. Pero entonces el balance deja
     * de poder explicarse: dentro de seis meses nadie sabria de donde salieron esos
     * $2.000 de diferencia. Como movimiento, el ajuste **se ve en la lista, dice
     * cuando se hizo, se puede editar o borrar, y se sincroniza** como cualquier
     * otro. Es la diferencia entre corregir y disimular.
     *
     * Devuelve por [onDone] los centavos ajustados (positivo si sobraba dinero,
     * negativo si faltaba) o null si no habia nada que cuadrar.
     */
    fun adjustBalance(
        realBalanceCents: Long,
        note: String = "",
        onDone: (Long?) -> Unit = {}
    ) {
        viewModelScope.launch {
            val ajuste = BalanceAdjustment.between(realBalanceCents, netBalance.value)
            if (ajuste.isNoOp) {
                onDone(null)
                return@launch
            }

            val esIngreso = ajuste.type == MovementType.INCOME
            val categoria = findOrCreateAdjustmentCategory(esIngreso)

            val ok = try {
                repository.insert(
                    MovementEntity(
                        type = ajuste.type.name,
                        amount = ajuste.amountCents,
                        paymentMethod = PaymentMethod.OTHER.name,
                        counterpartyRaw = "Ajuste de saldo",
                        categoryId = categoria,
                        date = System.currentTimeMillis(),
                        source = MovementSource.MANUAL.name,
                        confirmationState = ConfirmationState.CONFIRMED.name,
                        bankEntity = BankEntity.UNKNOWN.name,
                        rawText = note.ifBlank {
                            "Cuadre manual: el saldo real era ${realBalanceCents / 100} y Kivo " +
                                "tenia ${netBalance.value / 100}."
                        }
                    )
                )
                notifications.notify(
                    type = AppNotificationEntity.TYPE_SYSTEM,
                    title = "Saldo cuadrado",
                    message = "Se registro un ajuste de ${ajuste.amountCents / 100} pesos."
                )
                true
            } catch (e: Throwable) {
                Log.e(TAG, "No se pudo cuadrar el saldo", e)
                false
            }
            onDone(if (ok) ajuste.differenceCents else null)
        }
    }

    /**
     * La categoria del ajuste se busca por nombre y solo se crea si no existe, para
     * no sembrar una copia nueva cada vez que alguien cuadra el saldo — el mismo
     * problema de categorias duplicadas que ya se corrigio en DefaultCategories.
     */
    private suspend fun findOrCreateAdjustmentCategory(isIncome: Boolean): Long? = try {
        val tipo = (if (isIncome) MovementType.INCOME else MovementType.EXPENSE).name
        val nombre = "Ajuste de saldo"
        categoryRepository.getAll()
            .firstOrNull { it.name.equals(nombre, ignoreCase = true) && it.type == tipo }
            ?.id
            ?: categoryRepository.insert(
                CategoryEntity(
                    name = nombre,
                    type = tipo,
                    iconName = "build",
                    isCustom = true,
                    sortOrder = 9_000
                )
            ).takeIf { it > 0 }
    } catch (e: Throwable) {
        Log.w(TAG, "No se pudo preparar la categoria de ajuste; el ajuste queda sin clasificar", e)
        null
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

    private companion object {
        const val TAG = "MovementViewModel"
    }
}
