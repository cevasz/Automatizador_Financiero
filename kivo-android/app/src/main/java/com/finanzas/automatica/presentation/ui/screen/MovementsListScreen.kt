package com.finanzas.automatica.presentation.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.finanzas.automatica.presentation.ui.theme.KivoSpacing
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.R
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.domain.model.ConfirmationState
import com.finanzas.automatica.domain.model.Movement
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.presentation.ui.format.Money
import com.finanzas.automatica.presentation.ui.theme.KivoText
import com.finanzas.automatica.presentation.ui.components.AnimatedAmountText
import com.finanzas.automatica.presentation.ui.components.EmptyState
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.theme.ExpenseRose
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.InfoBlue
import com.finanzas.automatica.presentation.ui.theme.WarningAmber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class MovementFilter(val routeValue: String, val label: String) {
    All("all", "Todos"),
    Pending("pending", "Pendientes"),
    Income("income", "Ingresos"),
    Expenses("expense", "Gastos");

    companion object {
        fun fromRoute(value: String): MovementFilter {
            return entries.firstOrNull { it.routeValue == value } ?: All
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MovementsListScreen(
    movements: List<Movement>,
    initialFilter: String = "all",
    categories: List<Category> = emptyList(),
    onConfirm: (Long) -> Unit = {},
    onReject: (Long) -> Unit = {},
    onCorrect: (Long, Long) -> Unit = { _, _ -> },
    onCreateMovement: (MovementType, Long, String, Long?, Long) -> Unit = { _, _, _, _, _ -> },
    onUpdateMovement: (Long, MovementType, Long, String, Long?, Long) -> Unit = { _, _, _, _, _, _ -> },
    onDeleteMovement: (Long) -> Unit = {},
    onImportStatement: (String, com.finanzas.automatica.domain.model.BankEntity, (com.finanzas.automatica.domain.importer.ImportSummary) -> Unit) -> Unit = { _, _, _ -> },
    onImportPdf: (ByteArray, com.finanzas.automatica.domain.model.BankEntity, String?, (com.finanzas.automatica.domain.importer.ImportSummary) -> Unit, () -> Unit) -> Unit = { _, _, _, _, _ -> },
    onImportScreenshot: (android.net.Uri, (com.finanzas.automatica.domain.importer.ImportSummary) -> Unit) -> Unit = { _, _ -> },
    onOpenMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember(initialFilter) {
        mutableStateOf(MovementFilter.fromRoute(initialFilter))
    }
    var showImportDialog by remember { mutableStateOf(false) }
    var recategorizeTarget by remember { mutableStateOf<Movement?>(null) }
    var creandoMovimiento by remember { mutableStateOf(false) }

    val filteredMovements = remember(movements, selectedFilter) {
        movements
            .filter { movement ->
                when (selectedFilter) {
                    MovementFilter.All -> true
                    MovementFilter.Pending -> movement.confirmationState == ConfirmationState.PENDING
                    MovementFilter.Income -> movement.type == MovementType.INCOME
                    MovementFilter.Expenses -> movement.type == MovementType.EXPENSE
                }
            }
            .sortedByDescending { it.date }
    }

    if (showImportDialog) {
        ImportStatementDialog(
            onDismiss = { showImportDialog = false },
            onImport = { text, bank, onComplete ->
                onImportStatement(text, bank) { summary ->
                    onComplete(summary)
                }
            },
            onImportPdf = { bytes, bank, password, onComplete, onPasswordError ->
                onImportPdf(bytes, bank, password, { summary -> onComplete(summary) }, onPasswordError)
            },
            onImportScreenshot = { uri, onComplete ->
                onImportScreenshot(uri) { summary ->
                    onComplete(summary)
                }
            }
        )
    }

    // Tocar un movimiento ya no abre solo "recategorizar": abre el editor completo.
    // Poder cambiar la categoria pero no el monto dejaba sin salida el caso mas
    // comun de todos — que la cifra capturada no sea la correcta.
    recategorizeTarget?.let { movement ->
        MovementEditorDialog(
            existente = movement,
            categories = categories,
            onGuardar = { tipo, monto, contraparte, categoria, fecha ->
                onUpdateMovement(movement.id, tipo, monto, contraparte, categoria, fecha)
                recategorizeTarget = null
            },
            onEliminar = {
                onDeleteMovement(movement.id)
                recategorizeTarget = null
            },
            onCerrar = { recategorizeTarget = null }
        )
    }

    if (creandoMovimiento) {
        MovementEditorDialog(
            existente = null,
            categories = categories,
            onGuardar = { tipo, monto, contraparte, categoria, fecha ->
                onCreateMovement(tipo, monto, contraparte, categoria, fecha)
                creandoMovimiento = false
            },
            onCerrar = { creandoMovimiento = false }
        )
    }

    // Box + FAB en vez de otro boton en la barra superior: registrar un movimiento
    // es la accion mas repetida de esta pantalla y el skill de diseño pide que la
    // accion principal quede en el tercio inferior, al alcance del pulgar.
    Box(modifier = modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Movimientos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${filteredMovements.size} registros visibles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                androidx.compose.material3.OutlinedButton(
                    onClick = { showImportDialog = true },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.ReceiptLong, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Importar Extracto")
                }
            },
            navigationIcon = {
                onOpenMenu?.let {
                    IconButton(onClick = it) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Menu"
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MovementFilter.entries.forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter.label) }
                        )
                    }
                }
            }

            if (filteredMovements.isEmpty()) {
                item {
                    EmptyState(
                        icon = if (movements.isEmpty()) Icons.Outlined.ReceiptLong else Icons.Outlined.Tune,
                        title = if (movements.isEmpty()) "Sin movimientos" else "Sin resultados",
                        message = if (movements.isEmpty()) {
                            "Presiona 'Importar Extracto' para cargar movimientos en CSV/Texto/PDF/captura de pantalla o espera notificaciones."
                        } else {
                            "Cambia el filtro para revisar otros movimientos."
                        },
                        actionLabel = if (movements.isEmpty()) "Importar Extracto" else null,
                        onAction = if (movements.isEmpty()) { { showImportDialog = true } } else null,
                        illustrationRes = if (movements.isEmpty()) R.drawable.empty_state_wallet else null
                    )
                }
            } else {
                items(filteredMovements, key = { it.id }) { movement ->
                    SwipeableMovementCard(
                        movement = movement,
                        onClick = { recategorizeTarget = movement },
                        onConfirm = { onConfirm(movement.id) },
                        onReject = { onReject(movement.id) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }

        ExtendedFloatingActionButton(
            onClick = { creandoMovimiento = true },
            icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
            text = { Text("Registrar") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(KivoSpacing.lg)
        )
    }
}

@Composable
private fun ImportStatementDialog(
    onDismiss: () -> Unit,
    onImport: (String, com.finanzas.automatica.domain.model.BankEntity, (com.finanzas.automatica.domain.importer.ImportSummary) -> Unit) -> Unit,
    onImportPdf: (ByteArray, com.finanzas.automatica.domain.model.BankEntity, String?, (com.finanzas.automatica.domain.importer.ImportSummary) -> Unit, () -> Unit) -> Unit,
    onImportScreenshot: (android.net.Uri, (com.finanzas.automatica.domain.importer.ImportSummary) -> Unit) -> Unit = { _, _ -> }
) {
    var selectedBank by remember { mutableStateOf(com.finanzas.automatica.domain.model.BankEntity.BANCOLOMBIA) }
    var inputText by remember { mutableStateOf("") }
    var summaryResult by remember { mutableStateOf<com.finanzas.automatica.domain.importer.ImportSummary?>(null) }
    var isImported by remember { mutableStateOf(false) }
    var isPdfLoading by remember { mutableStateOf(false) }
    var isScreenshotLoading by remember { mutableStateOf(false) }
    var screenshotFeedback by remember { mutableStateOf<String?>(null) }

    // Extracto en PDF protegido con contraseña -- muy comun en bancos colombianos
    // (Bancolombia, Nequi, etc. suelen usar la cedula del titular). Se pide la
    // contraseña en un dialogo aparte en vez de fallar en silencio.
    var pendingPdfBytes by remember { mutableStateOf<ByteArray?>(null) }
    var showPdfPasswordDialog by remember { mutableStateOf(false) }
    var pdfPasswordError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun runPdfImport(bytes: ByteArray, password: String?) {
        isPdfLoading = true
        onImportPdf(
            bytes, selectedBank, password,
            { summary ->
                summaryResult = summary
                isImported = true
                isPdfLoading = false
                showPdfPasswordDialog = false
                pendingPdfBytes = null
            },
            {
                // Contraseña ausente/incorrecta: vuelve a pedirla en vez de reportar
                // una importacion fallida generica.
                isPdfLoading = false
                pendingPdfBytes = bytes
                pdfPasswordError = "Contraseña incorrecta. Intenta de nuevo."
                showPdfPasswordDialog = true
            }
        )
    }

    val pdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null && !isImported) {
            coroutineScope.launch {
                isPdfLoading = true
                val bytes = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }
                if (bytes != null && bytes.isNotEmpty()) {
                    val needsPassword = withContext(Dispatchers.Default) {
                        com.finanzas.automatica.domain.importer.PdfStatementExtractor.requiresPassword(bytes)
                    }
                    if (needsPassword) {
                        isPdfLoading = false
                        pendingPdfBytes = bytes
                        pdfPasswordError = null
                        showPdfPasswordDialog = true
                    } else {
                        runPdfImport(bytes, null)
                    }
                } else {
                    isPdfLoading = false
                }
            }
        }
    }
    val screenshotLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && !isImported) {
            isScreenshotLoading = true
            screenshotFeedback = null
            onImportScreenshot(uri) { summary ->
                isScreenshotLoading = false
                if (summary.totalCount > 0) {
                    summaryResult = summary
                    isImported = true
                } else {
                    screenshotFeedback = "No pudimos reconocer un movimiento en esa captura. Prueba con otra imagen o pega el texto manualmente."
                }
            }
        }
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Importar Extracto de Banco", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Pega las lineas de tu extracto bancario (CSV o texto plano de los ultimos 2 meses), sube el extracto en PDF, o escanea una captura de pantalla (OCR local, util para movimientos que no llegaron como notificacion):",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    com.finanzas.automatica.domain.model.BankEntity.entries.filter { it != com.finanzas.automatica.domain.model.BankEntity.UNKNOWN }.forEach { bank ->
                        FilterChip(
                            selected = selectedBank == bank,
                            onClick = { selectedBank = bank },
                            label = { Text(bank.name) }
                        )
                    }
                }

                androidx.compose.material3.OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Texto o renglones del extracto") },
                    placeholder = { Text("11/08/2026,Transferencia LUIS RINCON,100000\n10/08/2026,Compra Exito,-45000") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    maxLines = 6
                )

                androidx.compose.material3.OutlinedButton(
                    onClick = { pdfLauncher.launch(arrayOf("application/pdf")) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isImported
                ) {
                    if (isPdfLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(if (isPdfLoading) "Extrayendo texto del PDF..." else "Subir Extracto en PDF")
                }

                androidx.compose.material3.OutlinedButton(
                    onClick = { screenshotLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isImported && !isScreenshotLoading
                ) {
                    if (isScreenshotLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.ReceiptLong,
                            contentDescription = null
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(if (isScreenshotLoading) "Leyendo la captura..." else "Escanear Captura de Pantalla")
                }
                screenshotFeedback?.let { feedback ->
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodySmall,
                        color = WarningAmber
                    )
                }

                TextButton(onClick = {
                    inputText = """
                        11/08/2026, Transferencia LUIS RINCON, 100000
                        08/08/2026, Compra Supermercado Exito, -45000
                        01/08/2026, Pago Servicios EPM, -120000
                        25/07/2026, Abono de Nomina, 2500000
                        15/07/2026, Transferencia Nequi recibida, 80000
                    """.trimIndent()
                }) {
                    Text("Cargar ejemplo de los ultimos 2 meses")
                }

                AnimatedVisibility(
                    visible = summaryResult != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    summaryResult?.let { summary ->
                        FinanceCard(containerColor = IncomeGreen.copy(alpha = 0.1f)) {
                            Text("Resumen de importacion:", fontWeight = FontWeight.Bold)
                            Text("• Transacciones analizadas: ${summary.totalCount}")
                            Text("• Ingresos (${summary.incomeCount}): ${Money.format(summary.totalIncomeAmount)}")
                            Text("• Egresos (${summary.expenseCount}): ${Money.format(summary.totalExpenseAmount)}")
                        }
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = {
                    if (isImported) {
                        onDismiss()
                    } else if (inputText.isNotBlank()) {
                        onImport(inputText, selectedBank) { summary ->
                            summaryResult = summary
                            isImported = true
                        }
                    }
                },
                enabled = inputText.isNotBlank() || isImported
            ) {
                Text(if (isImported) "Listo" else "Procesar e Importar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    if (showPdfPasswordDialog) {
        PdfPasswordDialog(
            errorMessage = pdfPasswordError,
            isLoading = isPdfLoading,
            onDismiss = {
                showPdfPasswordDialog = false
                pendingPdfBytes = null
                pdfPasswordError = null
            },
            onSubmit = { password ->
                pdfPasswordError = null
                pendingPdfBytes?.let { runPdfImport(it, password) }
            }
        )
    }
}

/**
 * Extracto en PDF protegido con contraseña -- se centra en una `Card` de ancho acotado
 * (`widthIn(max = 420.dp)`) en vez de estirarse de borde a borde, para que se vea bien
 * tanto en celular como en tablet.
 */
@Composable
private fun PdfPasswordDialog(
    errorMessage: String?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.widthIn(max = 420.dp),
        icon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
        title = { Text("El PDF está protegido", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Los extractos bancarios en Colombia suelen venir con contraseña " +
                        "(a veces es tu número de cédula). Escríbela para poder leerlo.",
                    style = MaterialTheme.typography.bodySmall
                )
                androidx.compose.material3.OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña del PDF") },
                    singleLine = true,
                    enabled = !isLoading,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it, color = ExpenseRose) } },
                    visualTransformation = if (showPassword) {
                        androidx.compose.ui.text.input.VisualTransformation.None
                    } else {
                        androidx.compose.ui.text.input.PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = { onSubmit(password) },
                enabled = password.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Desbloquear")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Confirmacion ligera con gesto swipe (§6.10 del SDD, quedaba pendiente en
 * docs/PENDIENTES.md): deslizar a la derecha confirma, deslizar a la izquierda rechaza.
 * Los botones "Confirmar"/"Rechazar" siguen ahi debajo -- el swipe es un atajo mas rapido
 * para quien ya conoce el patron, no reemplaza la accion explicita (accesibilidad).
 * Solo aplica a movimientos PENDING; los demas se muestran sin gesto (nada que confirmar).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableMovementCard(
    movement: Movement,
    onClick: () -> Unit,
    onConfirm: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (movement.confirmationState != ConfirmationState.PENDING) {
        MovementCard(
            movement = movement,
            onClick = onClick,
            onConfirm = onConfirm,
            onReject = onReject,
            modifier = modifier
        )
        return
    }

    val haptics = LocalHapticFeedback.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onConfirm()
                    true
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onReject()
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        // Umbral mas alto que el default (50%->35% de la tarjeta): un gesto de
        // confirmacion financiera debe sentirse intencional, no un roce accidental.
        positionalThreshold = { totalDistance -> totalDistance * 0.35f }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = { SwipeActionBackground(dismissState.dismissDirection) }
    ) {
        MovementCard(
            movement = movement,
            onClick = onClick,
            onConfirm = onConfirm,
            onReject = onReject
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeActionBackground(direction: SwipeToDismissBoxValue) {
    val (color, icon, alignment, label) = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> SwipeBackgroundSpec(IncomeGreen, Icons.Outlined.CheckCircle, Alignment.CenterStart, "Confirmar")
        SwipeToDismissBoxValue.EndToStart -> SwipeBackgroundSpec(ExpenseRose, Icons.Outlined.Close, Alignment.CenterEnd, "Rechazar")
        SwipeToDismissBoxValue.Settled -> SwipeBackgroundSpec(Color.Transparent, null, Alignment.Center, "")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 24.dp),
        contentAlignment = alignment
    ) {
        if (icon != null) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (alignment == Alignment.CenterStart) {
                    Icon(icon, contentDescription = null, tint = color)
                    Text(label, color = color, fontWeight = FontWeight.SemiBold)
                } else {
                    Text(label, color = color, fontWeight = FontWeight.SemiBold)
                    Icon(icon, contentDescription = null, tint = color)
                }
            }
        }
    }
}

private data class SwipeBackgroundSpec(
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector?,
    val alignment: Alignment,
    val label: String
)

@Composable
private fun MovementCard(
    movement: Movement,
    onClick: () -> Unit,
    onConfirm: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isIncome = movement.type == MovementType.INCOME
    val statusColor = when (movement.confirmationState) {
        ConfirmationState.PENDING -> WarningAmber
        ConfirmationState.REJECTED -> ExpenseRose
        ConfirmationState.AUTO_CONFIRMED,
        ConfirmationState.CONFIRMED -> IncomeGreen
    }

    FinanceCard(
        modifier = modifier,
        containerColor = if (movement.confirmationState == ConfirmationState.PENDING) {
            WarningAmber.copy(alpha = 0.08f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBadge(
                    icon = if (isIncome) Icons.Outlined.TrendingUp else Icons.Outlined.TrendingDown,
                    contentDescription = movement.type.name,
                    tint = if (isIncome) IncomeGreen else InfoBlue
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = movement.counterpartyRaw,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${movement.bankEntity.name.cleanEnum()} / ${movement.shortDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            AnimatedAmountText(
                target = movement.amount,
                format = { animated ->
                    val prefix = if (isIncome) "+" else "-"
                    "$prefix${Money.format(animated)}"
                },
                style = KivoText.amount,
                color = if (isIncome) IncomeGreen else MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FinanceTag(
                text = movement.paymentMethod.name.cleanEnum(),
                color = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            FinanceTag(
                text = movement.confirmationState.name.cleanEnum(),
                color = statusColor,
                containerColor = statusColor.copy(alpha = 0.12f)
            )
        }

        if (movement.confirmationState == ConfirmationState.PENDING) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onReject) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Rechazar")
                }
                TextButton(onClick = onConfirm) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Confirmar")
                }
            }
        } else {
            TextButton(onClick = onClick, modifier = Modifier.align(Alignment.End)) {
                Text("Detalle")
            }
        }
    }
}


private fun Movement.shortDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "CO"))
        .withZone(ZoneId.systemDefault())
    return formatter.format(date)
}

private fun String.cleanEnum(): String {
    return lowercase()
        .replace('_', ' ')
        .replaceFirstChar { it.titlecase(Locale("es", "CO")) }
}
