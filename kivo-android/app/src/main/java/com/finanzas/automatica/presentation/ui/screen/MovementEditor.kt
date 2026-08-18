package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.domain.model.Movement
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.presentation.ui.components.AmountSize
import com.finanzas.automatica.presentation.ui.components.AmountText
import com.finanzas.automatica.presentation.ui.format.Money
import com.finanzas.automatica.presentation.ui.theme.ExpenseRose
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.KivoSpacing
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Alta y edicion de un movimiento a mano.
 *
 * Hasta ahora Kivo solo sabia registrar lo que llegaba por notificacion, OCR o
 * extracto: `MovementSource.MANUAL` existia en el modelo desde el principio pero
 * **ninguna pantalla lo producia**. Eso dejaba al usuario sin salida cuando algo
 * no se capturo (pago en efectivo, un banco que no manda notificacion) o cuando
 * lo capturado tenia el monto equivocado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementEditorDialog(
    existente: Movement?,
    categories: List<Category>,
    onGuardar: (MovementType, Long, String, Long?, Long) -> Unit,
    onEliminar: (() -> Unit)? = null,
    onCerrar: () -> Unit
) {
    var tipo by rememberSaveable { mutableStateOf(existente?.type ?: MovementType.EXPENSE) }
    var montoTexto by rememberSaveable {
        mutableStateOf(existente?.let { (it.amount / 100).toString() } ?: "")
    }
    var contraparte by rememberSaveable { mutableStateOf(existente?.counterpartyRaw.orEmpty()) }
    var categoriaId by rememberSaveable { mutableStateOf(existente?.categoryId) }
    var fecha by rememberSaveable {
        mutableStateOf(existente?.date?.toEpochMilli() ?: System.currentTimeMillis())
    }
    var eligiendoFecha by rememberSaveable { mutableStateOf(false) }

    val montoCentavos = Money.parsePesos(montoTexto)
    val compatibles = remember(categories, tipo) { categories.filter { it.type == tipo } }

    if (eligiendoFecha) {
        val estado = rememberDatePickerState(initialSelectedDateMillis = fecha)
        DatePickerDialog(
            onDismissRequest = { eligiendoFecha = false },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let { fecha = it }
                    eligiendoFecha = false
                }) { Text("Listo") }
            },
            dismissButton = { TextButton(onClick = { eligiendoFecha = false }) { Text("Cancelar") } }
        ) { DatePicker(state = estado) }
    }

    AlertDialog(
        onDismissRequest = onCerrar,
        modifier = Modifier.widthIn(max = KivoSpacing.dialogMaxWidth),
        title = { Text(if (existente == null) "Registrar movimiento" else "Editar movimiento") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(KivoSpacing.betweenGroups)
            ) {
                // El tipo primero: cambia que categorias tienen sentido debajo.
                Row(horizontalArrangement = Arrangement.spacedBy(KivoSpacing.sm)) {
                    FilterChip(
                        selected = tipo == MovementType.EXPENSE,
                        onClick = {
                            tipo = MovementType.EXPENSE
                            // La categoria elegida deja de valer al cambiar de tipo:
                            // dejarla puesta guardaria un gasto con categoria de ingreso.
                            categoriaId = null
                        },
                        label = { Text("Gasto") },
                        leadingIcon = { Icon(Icons.Outlined.TrendingDown, contentDescription = null) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ExpenseRose.copy(alpha = 0.2f))
                    )
                    FilterChip(
                        selected = tipo == MovementType.INCOME,
                        onClick = {
                            tipo = MovementType.INCOME
                            categoriaId = null
                        },
                        label = { Text("Ingreso") },
                        leadingIcon = { Icon(Icons.Outlined.TrendingUp, contentDescription = null) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IncomeGreen.copy(alpha = 0.2f))
                    )
                }

                OutlinedTextField(
                    value = montoTexto,
                    onValueChange = { montoTexto = it.filter(Char::isDigit).take(12) },
                    label = { Text("Monto en pesos") },
                    prefix = { Text("$") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = {
                        // Devuelve el monto ya formateado mientras se escribe: es la
                        // forma mas barata de que nadie registre $500.000 creyendo
                        // que puso $50.000.
                        Text(if (montoCentavos != null) Money.format(montoCentavos) else "Escribe solo numeros")
                    },
                    isError = montoTexto.isNotBlank() && montoCentavos == null,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = contraparte,
                    onValueChange = { contraparte = it },
                    label = { Text("¿A quién o de quién?") },
                    placeholder = { Text("Éxito, Juan, arriendo…") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                SelectorCategoria(
                    categorias = compatibles,
                    seleccion = categoriaId,
                    onSeleccion = { categoriaId = it }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Fecha: ${fechaLegible(fecha)}", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = { eligiendoFecha = true }) { Text("Cambiar") }
                }

                if (onEliminar != null) {
                    TextButton(onClick = onEliminar) {
                        Text("Eliminar este movimiento", color = ExpenseRose)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { montoCentavos?.let { onGuardar(tipo, it, contraparte, categoriaId, fecha) } },
                enabled = montoCentavos != null && montoCentavos > 0
            ) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}

/**
 * "Cuadrar saldo": el usuario dice cuanto tiene de verdad y Kivo registra la
 * diferencia como un movimiento de ajuste visible.
 *
 * Resuelve el caso que hasta ahora no tenia salida: "tengo $2.000 mas de los que
 * deberia y no hay forma de cuadrarlo". Antes la unica opcion era inventar un
 * movimiento falso a mano — y ni eso, porque tampoco se podian crear.
 */
@Composable
fun BalanceAdjustDialog(
    saldoActual: Long,
    onCuadrar: (Long, String) -> Unit,
    onCerrar: () -> Unit
) {
    var realTexto by rememberSaveable { mutableStateOf((saldoActual / 100).coerceAtLeast(0).toString()) }
    var nota by rememberSaveable { mutableStateOf("") }

    val realCentavos = Money.parsePesos(realTexto)
    val diferencia = realCentavos?.minus(saldoActual)

    AlertDialog(
        onDismissRequest = onCerrar,
        modifier = Modifier.widthIn(max = KivoSpacing.dialogMaxWidth),
        title = { Text("Cuadrar saldo") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(KivoSpacing.betweenGroups)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(KivoSpacing.betweenItems)) {
                    Text("Según Kivo tienes", style = MaterialTheme.typography.bodyMedium)
                    AmountText(cents = saldoActual, size = AmountSize.Hero)
                }

                OutlinedTextField(
                    value = realTexto,
                    onValueChange = { realTexto = it.filter(Char::isDigit).take(12) },
                    label = { Text("¿Cuánto tienes en realidad?") },
                    prefix = { Text("$") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = {
                        Text(if (realCentavos != null) Money.format(realCentavos) else "Escribe solo numeros")
                    },
                    isError = realTexto.isNotBlank() && realCentavos == null,
                    modifier = Modifier.fillMaxWidth()
                )

                // Se dice exactamente que se va a registrar ANTES de tocar el boton.
                // Un ajuste que aparece despues sin explicacion es indistinguible de
                // un error de la app.
                when {
                    diferencia == null -> Unit
                    diferencia == 0L -> Text(
                        "Ya está cuadrado: no hace falta ningún ajuste.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IncomeGreen
                    )
                    else -> Column(verticalArrangement = Arrangement.spacedBy(KivoSpacing.betweenItems)) {
                        Text(
                            if (diferencia > 0) "Se registrará un ingreso de ajuste por"
                            else "Se registrará un gasto de ajuste por",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        AmountText(
                            cents = kotlin.math.abs(diferencia),
                            color = if (diferencia > 0) IncomeGreen else ExpenseRose
                        )
                        Text(
                            "Queda en tu lista de movimientos, así que dentro de unos meses " +
                                "vas a poder ver de dónde salió esta diferencia.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = nota,
                    onValueChange = { nota = it },
                    label = { Text("Nota (opcional)") },
                    placeholder = { Text("Efectivo que no había registrado") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { realCentavos?.let { onCuadrar(it, nota) } },
                enabled = diferencia != null && diferencia != 0L
            ) {
                Text("Cuadrar")
            }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}

// FlowRow sigue marcada como experimental, pero es lo que hace que las categorias
// se acomoden solas en varias lineas: con un Row normal, alguien con muchas
// categorias propias veria las ultimas cortadas fuera de la pantalla.
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun SelectorCategoria(
    categorias: List<Category>,
    seleccion: Long?,
    onSeleccion: (Long?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(KivoSpacing.betweenItems)) {
        Text("Categoría", style = MaterialTheme.typography.labelMedium)
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(KivoSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(KivoSpacing.xs)
        ) {
            FilterChip(
                selected = seleccion == null,
                onClick = { onSeleccion(null) },
                label = { Text("Sin clasificar") }
            )
            categorias.forEach { categoria ->
                FilterChip(
                    selected = seleccion == categoria.id,
                    onClick = { onSeleccion(categoria.id) },
                    label = { Text(categoria.name) }
                )
            }
        }
    }
}

private fun fechaLegible(millis: Long): String =
    DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "CO"))
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(millis))
