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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.domain.model.ConfirmationState
import com.finanzas.automatica.domain.model.Movement
import com.finanzas.automatica.domain.model.MovementType
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
import java.text.NumberFormat
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
    onImportStatement: (String, com.finanzas.automatica.domain.model.BankEntity, (com.finanzas.automatica.domain.importer.ImportSummary) -> Unit) -> Unit = { _, _, _ -> },
    onImportPdf: (ByteArray, com.finanzas.automatica.domain.model.BankEntity, (com.finanzas.automatica.domain.importer.ImportSummary) -> Unit) -> Unit = { _, _, _ -> },
    onOpenMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }
    }
    var selectedFilter by remember(initialFilter) {
        mutableStateOf(MovementFilter.fromRoute(initialFilter))
    }
    var showImportDialog by remember { mutableStateOf(false) }
    var recategorizeTarget by remember { mutableStateOf<Movement?>(null) }

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
            currencyFormat = currencyFormat,
            onDismiss = { showImportDialog = false },
            onImport = { text, bank, onComplete ->
                onImportStatement(text, bank) { summary ->
                    onComplete(summary)
                }
            },
            onImportPdf = { bytes, bank, onComplete ->
                onImportPdf(bytes, bank) { summary ->
                    onComplete(summary)
                }
            }
        )
    }

    recategorizeTarget?.let { movement ->
        RecategorizeDialog(
            movement = movement,
            categories = categories.filter { it.type == movement.type },
            onDismiss = { recategorizeTarget = null },
            onConfirm = { categoryId ->
                onCorrect(movement.id, categoryId)
                recategorizeTarget = null
            }
        )
    }

    Column(
        modifier = modifier
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
                            "Presiona 'Importar Extracto' para cargar movimientos en CSV/Texto/PDF o espera notificaciones."
                        } else {
                            "Cambia el filtro para revisar otros movimientos."
                        },
                        actionLabel = if (movements.isEmpty()) "Importar Extracto" else null,
                        onAction = if (movements.isEmpty()) { { showImportDialog = true } } else null
                    )
                }
            } else {
                items(filteredMovements, key = { it.id }) { movement ->
                    MovementCard(
                        movement = movement,
                        currencyFormat = currencyFormat,
                        onClick = { recategorizeTarget = movement },
                        onConfirm = { onConfirm(movement.id) },
                        onReject = { onReject(movement.id) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

/**
 * Botón "Detalle" de un movimiento ya procesado: deja recategorizarlo (el motor de
 * reglas puede acertar la categoría equivocada). Antes este botón no tenía ningún
 * efecto -- MovementViewModel.correctMovement() ya existía pero nada lo llamaba.
 */
@Composable
private fun RecategorizeDialog(
    movement: Movement,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onConfirm: (categoryId: Long) -> Unit
) {
    var selectedCategoryId by remember(movement.id) { mutableStateOf(movement.categoryId) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recategorizar movimiento", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = movement.counterpartyRaw,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (categories.isEmpty()) {
                    Text(
                        text = "No hay categorías de este tipo todavía.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategoryId == category.id,
                                onClick = { selectedCategoryId = category.id },
                                label = { Text(category.name) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = { selectedCategoryId?.let(onConfirm) },
                enabled = selectedCategoryId != null && selectedCategoryId != movement.categoryId
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ImportStatementDialog(
    currencyFormat: NumberFormat,
    onDismiss: () -> Unit,
    onImport: (String, com.finanzas.automatica.domain.model.BankEntity, (com.finanzas.automatica.domain.importer.ImportSummary) -> Unit) -> Unit,
    onImportPdf: (ByteArray, com.finanzas.automatica.domain.model.BankEntity, (com.finanzas.automatica.domain.importer.ImportSummary) -> Unit) -> Unit
) {
    var selectedBank by remember { mutableStateOf(com.finanzas.automatica.domain.model.BankEntity.BANCOLOMBIA) }
    var inputText by remember { mutableStateOf("") }
    var summaryResult by remember { mutableStateOf<com.finanzas.automatica.domain.importer.ImportSummary?>(null) }
    var isImported by remember { mutableStateOf(false) }
    var isPdfLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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
                    onImportPdf(bytes, selectedBank) { summary ->
                        summaryResult = summary
                        isImported = true
                        isPdfLoading = false
                    }
                } else {
                    isPdfLoading = false
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
                    "Pega las lineas de tu extracto bancario (CSV o texto plano de los ultimos 2 meses) o sube el extracto en PDF:",
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
                            Text("• Ingresos (${summary.incomeCount}): ${currencyFormat.format(summary.totalIncomeAmount / 100.0)}")
                            Text("• Egresos (${summary.expenseCount}): ${currencyFormat.format(summary.totalExpenseAmount / 100.0)}")
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
}

@Composable
private fun MovementCard(
    movement: Movement,
    currencyFormat: NumberFormat,
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
                        fontWeight = FontWeight.SemiBold,
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
                    "$prefix${currencyFormat.money(animated)}"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
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

private fun NumberFormat.money(amount: Long): String = format(amount / 100.0)

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
