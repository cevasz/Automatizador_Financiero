package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.domain.model.AgendaEntry
import com.finanzas.automatica.domain.model.AgendaOrigin
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.presentation.ui.components.EmptyState
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AgendaScreen(
    agendaEntries: List<AgendaEntry>,
    categories: List<Category> = emptyList(),
    onEntryClick: (AgendaEntry) -> Unit = {},
    onAddEntry: () -> Unit = {},
    onOpenMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val filteredEntries = remember(agendaEntries, query) {
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) {
            agendaEntries.sortedBy { it.displayName }
        } else {
            agendaEntries
                .filter {
                    it.displayName.lowercase().contains(normalized) ||
                        it.accountIdentifier.lowercase().contains(normalized)
                }
                .sortedBy { it.displayName }
        }
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
                        text = "Agenda financiera",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${filteredEntries.size} contactos reconocibles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                IconButton(onClick = onAddEntry) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Agregar contacto"
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null
                        )
                    },
                    label = { Text("Buscar por nombre o cuenta") }
                )
            }

            if (filteredEntries.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        title = if (agendaEntries.isEmpty()) "Agenda vacia" else "Sin coincidencias",
                        message = if (agendaEntries.isEmpty()) {
                            "Agrega comercios o contactos frecuentes para mejorar la clasificacion automatica."
                        } else {
                            "Prueba con otro nombre, telefono, correo o cuenta."
                        },
                        actionLabel = if (agendaEntries.isEmpty()) "Agregar contacto" else null,
                        onAction = if (agendaEntries.isEmpty()) onAddEntry else null
                    )
                }
            } else {
                items(filteredEntries, key = { it.id }) { entry ->
                    val category = categories.find { it.id == entry.defaultCategoryId }
                    AgendaEntryCard(
                        entry = entry,
                        category = category,
                        onClick = { onEntryClick(entry) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

@Composable
fun AgendaEntryCard(
    entry: AgendaEntry,
    category: Category?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FinanceCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBadge(
                    icon = Icons.Outlined.Business,
                    contentDescription = entry.displayName,
                    tint = Color(entry.color)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = entry.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = entry.accountIdentifier,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Surface(
                modifier = Modifier.size(18.dp),
                color = Color(entry.color),
                shape = CircleShape
            ) {}
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FinanceTag(
                text = category?.name ?: "Sin categoria",
                color = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            FinanceTag(
                text = entry.origin.name.cleanEnum(),
                color = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    }
}

@Composable
fun AddEditAgendaEntryScreen(
    entry: AgendaEntry?,
    categories: List<Category>,
    onSave: (AgendaEntry) -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var identifier by remember { mutableStateOf(entry?.accountIdentifier ?: "") }
    var displayName by remember { mutableStateOf(entry?.displayName ?: "") }
    var selectedCategoryId by remember { mutableStateOf(entry?.defaultCategoryId) }
    var selectedColor by remember { mutableStateOf(entry?.color ?: 0xFF6F8F7B.toInt()) }
    val colorOptions = listOf(
        0xFF6F8F7B.toInt(),
        0xFF8A7EAD.toInt(),
        0xFFC9828A.toInt(),
        0xFF6F92B7.toInt(),
        0xFFC79A4C.toInt()
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (entry == null) "Nuevo contacto" else "Editar contacto",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                label = { Text("Nombre visible") }
            )
        }

        item {
            OutlinedTextField(
                value = identifier,
                onValueChange = { identifier = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                label = { Text("Telefono, correo o cuenta") }
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Categoria por defecto",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { selectedCategoryId = null },
                        label = { Text("Sin categoria") }
                    )
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

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    colorOptions.forEach { color ->
                        Surface(
                            modifier = Modifier
                                .size(42.dp)
                                .clickable { selectedColor = color },
                            color = Color(color),
                            shape = CircleShape,
                            border = BorderStroke(
                                width = if (selectedColor == color) 3.dp else 1.dp,
                                color = if (selectedColor == color) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                }
                            )
                        ) {}
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        onSave(
                            AgendaEntry(
                                id = entry?.id ?: 0,
                                accountIdentifier = identifier.trim(),
                                displayName = displayName.trim(),
                                defaultCategoryId = selectedCategoryId,
                                color = selectedColor,
                                origin = entry?.origin ?: AgendaOrigin.MANUAL,
                                createdAt = entry?.createdAt ?: java.time.Instant.now()
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = identifier.isNotBlank() && displayName.isNotBlank(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Guardar")
                }
            }
        }

        if (onDelete != null) {
            item {
                Spacer(Modifier.height(4.dp))
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Eliminar contacto")
                }
            }
        }
    }
}

private fun String.cleanEnum(): String {
    return lowercase()
        .replace('_', ' ')
        .replaceFirstChar { it.titlecase(java.util.Locale("es", "CO")) }
}
