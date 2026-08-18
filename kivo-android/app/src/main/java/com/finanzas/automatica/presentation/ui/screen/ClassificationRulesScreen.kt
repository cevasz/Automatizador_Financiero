package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Rule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.finanzas.automatica.data.local.entity.ClassificationRuleEntity
import com.finanzas.automatica.presentation.ui.components.EmptyState
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.theme.ExpenseRose
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.WarningAmber
import com.finanzas.automatica.presentation.viewmodel.ClassificationRulesViewModel

private val BANCOS = listOf("NEQUI", "BANCOLOMBIA", "DAVIPLATA", "NU", "LULO", "UNKNOWN")

private val ETIQUETA_BANCO = mapOf(
    "NEQUI" to "Nequi",
    "BANCOLOMBIA" to "Bancolombia",
    "DAVIPLATA" to "Daviplata",
    "NU" to "Nu",
    "LULO" to "Lulo Bank",
    "UNKNOWN" to "Sin identificar"
)

/**
 * Reglas de clasificacion: "si el texto del banco contiene X, clasifícalo como Y".
 *
 * El motor ya las usaba (paso 2 de `DefaultClassificationEngine`), pero no habia
 * forma de crearlas, asi que la tabla siempre estaba vacia.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassificationRulesScreen(
    rules: List<ClassificationRuleEntity>,
    categories: List<CategoryEntity>,
    error: String?,
    onSave: (Long?, String, String, Long, Int, Boolean) -> Unit,
    onSetActive: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit,
    onConsumeError: () -> Unit,
    onOpenMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var editando by remember { mutableStateOf<ClassificationRuleEntity?>(null) }
    var creando by rememberSaveable { mutableStateOf(false) }

    val nombreCategoria = remember(categories) { categories.associate { it.id to it.name } }
    val ordenadas = remember(rules) {
        rules.sortedWith(compareBy({ it.bankEntity }, { -it.priority }))
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Reglas de clasificación",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${rules.count { it.isActive }} activa(s) de ${rules.size}",
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
                Icon(Icons.Outlined.Add, contentDescription = "Nueva regla")
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
            error?.let {
                FinanceCard(containerColor = MaterialTheme.colorScheme.errorContainer) {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = onConsumeError) { Text("Entendido") }
                }
            }

            if (ordenadas.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.Rule,
                    title = "Sin reglas propias",
                    message = "Una regla clasifica sola los movimientos cuyo texto coincida. " +
                        "Por ejemplo: si dice \"RAPPI\", que sea Comida rápida.",
                    actionLabel = "Crear la primera",
                    onAction = { creando = true }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
                ) {
                    items(ordenadas, key = { it.id }) { regla ->
                        FilaRegla(
                            regla = regla,
                            categoria = nombreCategoria[regla.categoryId] ?: "Categoría eliminada",
                            onEditar = { editando = regla },
                            onActivar = { onSetActive(regla.id, it) },
                            onBorrar = { onDelete(regla.id) }
                        )
                    }
                }
            }
        }
    }

    if (creando || editando != null) {
        DialogoRegla(
            regla = editando,
            categories = categories,
            onGuardar = { pattern, banco, categoria, prioridad, activa ->
                onSave(editando?.id, pattern, banco, categoria, prioridad, activa)
                creando = false
                editando = null
            },
            onCerrar = {
                creando = false
                editando = null
            }
        )
    }
}

@Composable
private fun FilaRegla(
    regla: ClassificationRuleEntity,
    categoria: String,
    onEditar: () -> Unit,
    onActivar: (Boolean) -> Unit,
    onBorrar: () -> Unit
) {
    FinanceCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = regla.pattern,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "→ $categoria",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = regla.isActive, onCheckedChange = onActivar)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            FinanceTag(
                text = ETIQUETA_BANCO[regla.bankEntity] ?: regla.bankEntity,
                color = IncomeGreen,
                containerColor = IncomeGreen.copy(alpha = 0.12f)
            )
            FinanceTag(
                text = "Prioridad ${regla.priority}",
                color = WarningAmber,
                containerColor = WarningAmber.copy(alpha = 0.12f)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Editar regla")
                }
                IconButton(onClick = onBorrar) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Eliminar regla", tint = ExpenseRose)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoRegla(
    regla: ClassificationRuleEntity?,
    categories: List<CategoryEntity>,
    onGuardar: (String, String, Long, Int, Boolean) -> Unit,
    onCerrar: () -> Unit
) {
    var pattern by rememberSaveable { mutableStateOf(regla?.pattern.orEmpty()) }
    var banco by rememberSaveable { mutableStateOf(regla?.bankEntity ?: "NEQUI") }
    var categoriaId by rememberSaveable {
        mutableStateOf(regla?.categoryId ?: categories.firstOrNull()?.id ?: 0L)
    }
    var prioridad by rememberSaveable { mutableStateOf((regla?.priority ?: 0).toString()) }
    var activa by rememberSaveable { mutableStateOf(regla?.isActive ?: true) }
    var textoPrueba by rememberSaveable { mutableStateOf("") }

    val problema = ClassificationRulesViewModel.validar(pattern)
    val coincidencia = if (textoPrueba.isNotBlank() && problema == null) {
        ClassificationRulesViewModel.coincide(pattern, textoPrueba)
    } else {
        null
    }

    AlertDialog(
        onDismissRequest = onCerrar,
        modifier = Modifier.widthIn(max = 480.dp),
        title = { Text(if (regla == null) "Nueva regla" else "Editar regla") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = pattern,
                    onValueChange = { pattern = it },
                    label = { Text("Texto o expresión a buscar") },
                    supportingText = {
                        Text(
                            problema ?: "Se busca dentro del texto de la notificación. " +
                                "Escribir RAPPI basta; también funcionan expresiones regulares."
                        )
                    },
                    isError = problema != null && pattern.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                Selector(
                    etiqueta = "Banco",
                    opciones = BANCOS.map { it to (ETIQUETA_BANCO[it] ?: it) },
                    seleccion = banco,
                    onSeleccion = { banco = it },
                    // El motor busca las reglas del banco del movimiento
                    // (getActiveRulesForBank), asi que una regla solo aplica al
                    // banco elegido. No hay opcion "todos" a proposito: seria
                    // mentira mostrarla y que no funcione.
                    ayuda = "La regla solo se aplica a movimientos de este banco."
                )

                Selector(
                    etiqueta = "Categoría",
                    opciones = categories.map { it.id.toString() to "${it.name} (${if (it.type == "INCOME") "ingreso" else "gasto"})" },
                    seleccion = categoriaId.toString(),
                    onSeleccion = { categoriaId = it.toLongOrNull() ?: categoriaId }
                )

                OutlinedTextField(
                    value = prioridad,
                    onValueChange = { prioridad = it.filter(Char::isDigit).take(3) },
                    label = { Text("Prioridad") },
                    supportingText = { Text("Se prueban de mayor a menor. Gana la primera que coincida.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Probador: una expresion mal escrita no falla al guardarla sino
                // silenciosamente al clasificar, meses despues y sin sintoma.
                OutlinedTextField(
                    value = textoPrueba,
                    onValueChange = { textoPrueba = it },
                    label = { Text("Probar con un texto de ejemplo") },
                    supportingText = {
                        // Sin ✓/✗: se verificó en el archivo de la fuente que Manrope
                        // no trae esos dos glifos, así que Android los pintaría como
                        // un rectángulo vacío. El color ya distingue los dos casos.
                        Text(
                            text = when (coincidencia) {
                                true -> "Sí coincide: esta regla clasificaría ese movimiento."
                                false -> "No coincide con ese texto."
                                null -> "Pega aquí una notificación real para comprobarlo."
                            },
                            color = when (coincidencia) {
                                true -> IncomeGreen
                                false -> ExpenseRose
                                null -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Regla activa")
                    Switch(checked = activa, onCheckedChange = { activa = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(pattern, banco, categoriaId, prioridad.toIntOrNull() ?: 0, activa) },
                enabled = problema == null && categoriaId > 0L
            ) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Selector(
    etiqueta: String,
    opciones: List<Pair<String, String>>,
    seleccion: String,
    onSeleccion: (String) -> Unit,
    ayuda: String? = null
) {
    var abierto by remember { mutableStateOf(false) }
    val visible = opciones.firstOrNull { it.first == seleccion }?.second ?: "Elegir…"

    ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = it }) {
        OutlinedTextField(
            value = visible,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            supportingText = ayuda?.let { { Text(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { (valor, texto) ->
                DropdownMenuItem(
                    text = { Text(texto) },
                    onClick = {
                        onSeleccion(valor)
                        abierto = false
                    }
                )
            }
        }
    }
}
