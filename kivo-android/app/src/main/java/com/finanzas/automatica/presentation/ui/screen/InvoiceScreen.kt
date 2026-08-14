package com.finanzas.automatica.presentation.ui.screen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Scanner
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.domain.model.AgendaEntry
import com.finanzas.automatica.domain.model.DebtStatus
import com.finanzas.automatica.domain.model.DebtSummary
import com.finanzas.automatica.domain.model.Invoice
import com.finanzas.automatica.domain.model.InvoiceItem
import com.finanzas.automatica.presentation.ui.components.EmptyState
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.components.SectionHeader
import com.finanzas.automatica.presentation.ui.theme.ExpenseRose
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.InfoBlue
import com.finanzas.automatica.presentation.ui.theme.WarningAmber
import java.text.NumberFormat
import java.util.Locale

private enum class InvoiceTab(val label: String) {
    UPLOAD("Subir / Clasificar"),
    DEBTS("Deudas Por Cobrar"),
    INVOICES("Facturas Guardadas")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InvoiceScreen(
    invoices: List<Invoice>,
    debtSummaries: List<DebtSummary>,
    allDebts: List<InvoiceItem>,
    contacts: List<AgendaEntry>,
    onSaveInvoice: (merchantName: String, totalAmount: Long, items: List<InvoiceItem>) -> Unit,
    onMarkDebtPaid: (Long) -> Unit,
    onDeleteInvoice: (Long) -> Unit,
    onSimulateScan: () -> Pair<String, List<InvoiceItem>>,
    onOpenMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }
    }
    var currentTab by remember { mutableStateOf(InvoiceTab.UPLOAD) }

    // Estado local para la creación de factura
    var merchantName by remember { mutableStateOf("") }
    val draftItems = remember { mutableStateListOf<InvoiceItem>() }
    var showSuccessMessage by remember { mutableStateOf(false) }

    val totalInvoiceAmount = remember(draftItems) {
        draftItems.sumOf { it.totalPrice }
    }
    val totalDebtAmount = remember(draftItems) {
        draftItems.filter { it.isDebt }.sumOf { it.totalPrice }
    }
    val totalPersonalAmount = totalInvoiceAmount - totalDebtAmount

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Facturas y Deudas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Clasifica los productos y asigna deudas por cobrar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InvoiceTab.entries.forEach { tab ->
                        FilterChip(
                            selected = currentTab == tab,
                            onClick = { currentTab = tab },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }

            when (currentTab) {
                InvoiceTab.UPLOAD -> {
                    item {
                        FinanceCard(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconBadge(
                                        icon = Icons.Outlined.Scanner,
                                        contentDescription = "OCR Factura",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Column {
                                        Text(
                                            text = "Escaneo / Carga de Factura",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Sube tu recibo o simula la lectura automática",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                OutlinedButton(
                                    onClick = {
                                        val sample = onSimulateScan()
                                        merchantName = sample.first
                                        draftItems.clear()
                                        draftItems.addAll(sample.second)
                                    }
                                ) {
                                    Icon(Icons.Outlined.Scanner, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Simular Escaneo")
                                }
                            }
                        }
                    }

                    item {
                        FinanceCard {
                            Text(
                                text = "1. Comercio o Establecimiento",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            OutlinedTextField(
                                value = merchantName,
                                onValueChange = { merchantName = it },
                                label = { Text("Nombre del comercio / restaurante") },
                                placeholder = { Text("Ej: Restaurante El Corral, Éxito") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        SectionHeader(
                            title = "2. Desglose de Productos",
                            action = {
                                TextButton(
                                    onClick = {
                                        draftItems.add(
                                            InvoiceItem(
                                                productName = "Nuevo Producto",
                                                quantity = 1,
                                                unitPrice = 1000000, // $10.000 COP
                                                totalPrice = 1000000,
                                                isDebt = false
                                            )
                                        )
                                    }
                                ) {
                                    Icon(Icons.Outlined.Add, contentDescription = null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Agregar Producto")
                                }
                            }
                        )
                    }

                    if (draftItems.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Outlined.ShoppingBag,
                                title = "Sin productos agregados",
                                message = "Presiona 'Simular Escaneo' o 'Agregar Producto' para desglosar la factura.",
                                actionLabel = "Cargar Ejemplo",
                                onAction = {
                                    val sample = onSimulateScan()
                                    merchantName = sample.first
                                    draftItems.clear()
                                    draftItems.addAll(sample.second)
                                }
                            )
                        }
                    } else {
                        // Sin `key` explícita: los items en borrador aún no tienen id propio
                        // (id = 0 por defecto hasta guardarse), así que una key basada en id
                        // duplicaría claves y haría fallar el LazyColumn.
                        items(draftItems.size) { index ->
                            val item = draftItems[index]
                            InvoiceItemEditorCard(
                                item = item,
                                contacts = contacts,
                                currencyFormat = currencyFormat,
                                onItemChange = { updated ->
                                    draftItems[index] = updated
                                },
                                onDelete = {
                                    draftItems.removeAt(index)
                                },
                                modifier = Modifier.animateItemPlacement()
                            )
                        }

                        item {
                            FinanceCard(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) {
                                Text(
                                    text = "Resumen de Clasificación",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total Factura:", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        currencyFormat.format(totalInvoiceAmount / 100.0),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Tu gasto personal:", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        currencyFormat.format(totalPersonalAmount / 100.0),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total Deudas por Cobrar:", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        currencyFormat.format(totalDebtAmount / 100.0),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = IncomeGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        if (merchantName.isNotBlank() && draftItems.isNotEmpty()) {
                                            onSaveInvoice(merchantName, totalInvoiceAmount, draftItems.toList())
                                            showSuccessMessage = true
                                            merchantName = ""
                                            draftItems.clear()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = merchantName.isNotBlank() && draftItems.isNotEmpty(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Guardar Factura y Registrar Deudas")
                                }

                                if (showSuccessMessage) {
                                    FinanceTag(
                                        text = "Factura guardada correctamente con sus deudas registradas.",
                                        color = IncomeGreen,
                                        containerColor = IncomeGreen.copy(alpha = 0.12f),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                InvoiceTab.DEBTS -> {
                    item {
                        FinanceCard(containerColor = IncomeGreen.copy(alpha = 0.08f)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconBadge(
                                    icon = Icons.Outlined.Person,
                                    contentDescription = "Deudas",
                                    tint = IncomeGreen
                                )
                                Column {
                                    Text(
                                        text = "Cuentas por Cobrar",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    val totalAllOwed = debtSummaries.sumOf { it.totalOwed }
                                    Text(
                                        text = "Te deben en total: ${currencyFormat.format(totalAllOwed / 100.0)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = IncomeGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    if (debtSummaries.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Outlined.Person,
                                title = "Sin deudas pendientes",
                                message = "Al subir o clasificar productos de facturas como deudas de tus contactos, aparecerán aquí para que lleves el control.",
                                actionLabel = "Clasificar Factura",
                                onAction = { currentTab = InvoiceTab.UPLOAD }
                            )
                        }
                    } else {
                        items(debtSummaries, key = { it.debtorName }) { summary ->
                            DebtSummaryCard(
                                summary = summary,
                                currencyFormat = currencyFormat,
                                onMarkItemPaid = onMarkDebtPaid,
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                }

                InvoiceTab.INVOICES -> {
                    if (invoices.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Outlined.ReceiptLong,
                                title = "No hay facturas guardadas",
                                message = "Sube o registra tu primera factura para llevar el desglose de consumos y deudas.",
                                actionLabel = "Subir Factura",
                                onAction = { currentTab = InvoiceTab.UPLOAD }
                            )
                        }
                    } else {
                        items(invoices, key = { it.id }) { invoice ->
                            SavedInvoiceCard(
                                invoice = invoice,
                                currencyFormat = currencyFormat,
                                onDelete = { onDeleteInvoice(invoice.id) },
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvoiceItemEditorCard(
    item: InvoiceItem,
    contacts: List<AgendaEntry>,
    currencyFormat: NumberFormat,
    onItemChange: (InvoiceItem) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    FinanceCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Producto",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Eliminar",
                    tint = ExpenseRose
                )
            }
        }

        OutlinedTextField(
            value = item.productName,
            onValueChange = { onItemChange(item.copy(productName = it)) },
            label = { Text("Nombre del producto / consumo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = (item.totalPrice / 100).toString(),
                onValueChange = { val str = it.filter { c -> c.isDigit() }; val price = (str.toLongOrNull() ?: 0) * 100; onItemChange(item.copy(unitPrice = price, totalPrice = price)) },
                label = { Text("Precio (COP)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        // Clasificación de Deuda
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (item.isDebt) IncomeGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¿Alguien te debe este producto?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (item.isDebt) "Marcar como Deuda por Cobrar" else "Gasto personal propio",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (item.isDebt) IncomeGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = item.isDebt,
                        onCheckedChange = { isChecked ->
                            onItemChange(item.copy(isDebt = isChecked))
                        }
                    )
                }

                if (item.isDebt) {
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = item.debtorName ?: "",
                            onValueChange = { name -> onItemChange(item.copy(debtorName = name)) },
                            label = { Text("¿Quién te debe este dinero?") },
                            placeholder = { Text("Escribe o selecciona de la agenda") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            singleLine = true
                        )

                        if (contacts.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                contacts.forEach { contact ->
                                    DropdownMenuItem(
                                        text = { Text(contact.displayName) },
                                        onClick = {
                                            onItemChange(item.copy(debtorName = contact.displayName, debtorContactId = contact.id))
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = item.notes ?: "",
                        onValueChange = { onItemChange(item.copy(notes = it)) },
                        label = { Text("Notas / Detalle opcional") },
                        placeholder = { Text("Ej: Le compré la hamburguesa en el almuerzo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}

@Composable
private fun DebtSummaryCard(
    summary: DebtSummary,
    currencyFormat: NumberFormat,
    onMarkItemPaid: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    FinanceCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBadge(
                    icon = Icons.Outlined.Person,
                    contentDescription = summary.debtorName,
                    tint = IncomeGreen
                )
                Column {
                    Text(
                        text = summary.debtorName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${summary.itemsCount} producto(s) pendiente(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = currencyFormat.format(summary.totalOwed / 100.0),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = IncomeGreen
            )
        }

        Spacer(Modifier.height(4.dp))

        summary.pendingItems.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.productName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!item.notes.isNullOrBlank()) {
                        Text(
                            text = item.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = currencyFormat.format(item.totalPrice / 100.0),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onMarkItemPaid(item.id) }) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = IncomeGreen)
                        Spacer(Modifier.width(4.dp))
                        Text("Cobrado", color = IncomeGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedInvoiceCard(
    invoice: Invoice,
    currencyFormat: NumberFormat,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val debtItems = invoice.items.filter { it.isDebt }
    val debtTotal = debtItems.sumOf { it.totalPrice }
    val personalTotal = invoice.totalAmount - debtTotal

    FinanceCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = invoice.merchantName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${invoice.items.size} producto(s) desglosados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = currencyFormat.format(invoice.totalAmount / 100.0),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Eliminar", tint = ExpenseRose)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FinanceTag(
                text = "Tu gasto: ${currencyFormat.format(personalTotal / 100.0)}",
                color = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            if (debtTotal > 0) {
                FinanceTag(
                    text = "Deudas: ${currencyFormat.format(debtTotal / 100.0)}",
                    color = IncomeGreen,
                    containerColor = IncomeGreen.copy(alpha = 0.12f)
                )
            }
        }

        invoice.items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "• ${item.productName}" + if (item.isDebt) " (Deuda de: ${item.debtorName})" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (item.isDebt) IncomeGreen else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = currencyFormat.format(item.totalPrice / 100.0),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
