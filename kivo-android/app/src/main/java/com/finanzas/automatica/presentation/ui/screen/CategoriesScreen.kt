package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.presentation.ui.components.CategoryIcons
import com.finanzas.automatica.presentation.ui.components.EmptyState
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.theme.ExpenseRose
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.viewmodel.CategoryImpact

/**
 * Administracion de categorias.
 *
 * Hasta esta version las 33 categorias sembradas al instalar eran las unicas
 * que podian existir: `isCustom` estaba en el modelo desde el principio, pero
 * ninguna pantalla permitia crear, renombrar ni eliminar una.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoriesScreen(
    categories: List<CategoryEntity>,
    impacts: Map<Long, CategoryImpact>,
    error: String?,
    onCreate: (String, String, String) -> Unit,
    onUpdate: (Long, String, String) -> Unit,
    onDelete: (Long) -> Unit,
    onConsumeError: () -> Unit,
    onOpenMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var tipoVisible by rememberSaveable { mutableStateOf("EXPENSE") }
    var editando by remember { mutableStateOf<CategoryEntity?>(null) }
    var creando by rememberSaveable { mutableStateOf(false) }
    var porBorrar by remember { mutableStateOf<CategoryEntity?>(null) }

    val visibles = remember(categories, tipoVisible) {
        categories.filter { it.type == tipoVisible }.sortedWith(
            compareBy({ it.sortOrder }, { it.name })
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Categorías",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${categories.count { it.isCustom }} propias · ${categories.size} en total",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    onOpenMenu?.let {
                        IconButton(onClick = it) { Icon(Icons.Outlined.Menu, contentDescription = "Menu") }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { creando = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Nueva categoría")
            }
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("EXPENSE" to "Gastos", "INCOME" to "Ingresos").forEach { (valor, etiqueta) ->
                    FilterChip(
                        selected = tipoVisible == valor,
                        onClick = { tipoVisible = valor },
                        label = { Text(etiqueta) },
                        colors = FilterChipDefaults.filterChipColors()
                    )
                }
            }

            error?.let {
                FinanceCard(containerColor = MaterialTheme.colorScheme.errorContainer) {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = onConsumeError) { Text("Entendido") }
                }
            }

            if (visibles.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.Category,
                    title = "Sin categorías",
                    message = "Crea la primera para empezar a clasificar tus movimientos.",
                    actionLabel = "Nueva categoría",
                    onAction = { creando = true }
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(visibles, key = { it.id }) { categoria ->
                        FilaCategoria(
                            categoria = categoria,
                            impacto = impacts[categoria.id] ?: CategoryImpact(),
                            onEditar = { editando = categoria },
                            onBorrar = { porBorrar = categoria }
                        )
                    }
                }
            }
        }
    }

    if (creando) {
        DialogoCategoria(
            titulo = "Nueva categoría",
            tipoInicial = tipoVisible,
            permitirCambiarTipo = true,
            onConfirmar = { nombre, tipo, icono ->
                onCreate(nombre, tipo, icono)
                creando = false
            },
            onCerrar = { creando = false }
        )
    }

    editando?.let { categoria ->
        DialogoCategoria(
            titulo = "Editar categoría",
            nombreInicial = categoria.name,
            iconoInicial = categoria.iconName,
            tipoInicial = categoria.type,
            // El tipo no se puede cambiar: una categoria de gasto con movimientos
            // e historial detras no puede volverse de ingreso sin dejar todos esos
            // movimientos (y sus presupuestos) contradiciendose.
            permitirCambiarTipo = false,
            onConfirmar = { nombre, _, icono ->
                onUpdate(categoria.id, nombre, icono)
                editando = null
            },
            onCerrar = { editando = null }
        )
    }

    porBorrar?.let { categoria ->
        val impacto = impacts[categoria.id] ?: CategoryImpact()
        AlertDialog(
            onDismissRequest = { porBorrar = null },
            title = { Text("¿Eliminar \"${categoria.name}\"?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (impacto.isEmpty) {
                        Text("No hay nada asociado a esta categoría.")
                    } else {
                        Text("Esto también va a pasar:")
                        if (impacto.movements > 0) {
                            Text("• ${impacto.movements} movimiento(s) quedarán sin clasificar.")
                        }
                        if (impacto.budgets > 0) {
                            Text("• Se eliminarán ${impacto.budgets} presupuesto(s) de esta categoría.")
                        }
                        if (impacto.rules > 0) {
                            Text("• Se eliminarán ${impacto.rules} regla(s) de clasificación.")
                        }
                        if (impacto.agendaEntries > 0) {
                            Text("• ${impacto.agendaEntries} contacto(s) de la agenda perderán su categoría por defecto.")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(categoria.id)
                    porBorrar = null
                }) {
                    Text("Eliminar", color = ExpenseRose)
                }
            },
            dismissButton = {
                TextButton(onClick = { porBorrar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun FilaCategoria(
    categoria: CategoryEntity,
    impacto: CategoryImpact,
    onEditar: () -> Unit,
    onBorrar: () -> Unit
) {
    val acento = if (categoria.type == "INCOME") IncomeGreen else ExpenseRose

    FinanceCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(
                icon = CategoryIcons.resolve(categoria.iconName),
                contentDescription = null,
                tint = acento
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = categoria.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (impacto.movements > 0) {
                        "${impacto.movements} movimiento(s)"
                    } else {
                        "Sin movimientos todavía"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!categoria.isCustom) {
                FinanceTag(
                    text = "De fábrica",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            IconButton(onClick = onEditar) {
                Icon(Icons.Outlined.Edit, contentDescription = "Editar ${categoria.name}")
            }
            IconButton(onClick = onBorrar) {
                Icon(Icons.Outlined.Delete, contentDescription = "Eliminar ${categoria.name}", tint = ExpenseRose)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DialogoCategoria(
    titulo: String,
    nombreInicial: String = "",
    iconoInicial: String = "more_horiz",
    tipoInicial: String = "EXPENSE",
    permitirCambiarTipo: Boolean,
    onConfirmar: (String, String, String) -> Unit,
    onCerrar: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf(nombreInicial) }
    var tipo by rememberSaveable { mutableStateOf(tipoInicial) }
    var icono by rememberSaveable { mutableStateOf(iconoInicial) }

    AlertDialog(
        onDismissRequest = onCerrar,
        // Acotado para que en tablet el dialogo no se estire de lado a lado.
        modifier = Modifier.widthIn(max = 460.dp),
        title = { Text(titulo) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (permitirCambiarTipo) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("EXPENSE" to "Gasto", "INCOME" to "Ingreso").forEach { (valor, etiqueta) ->
                            FilterChip(
                                selected = tipo == valor,
                                onClick = { tipo = valor },
                                label = { Text(etiqueta) }
                            )
                        }
                    }
                }

                Text("Icono", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CategoryIcons.seleccionables.forEach { nombreIcono ->
                        val seleccionado = nombreIcono == icono
                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .selectable(selected = seleccionado, onClick = { icono = nombreIcono }),
                            shape = CircleShape,
                            color = if (seleccionado) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = CategoryIcons.resolve(nombreIcono),
                                    contentDescription = nombreIcono,
                                    modifier = Modifier.size(22.dp),
                                    tint = if (seleccionado) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmar(nombre, tipo, icono) },
                enabled = nombre.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    )
}
