package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Troubleshoot
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.data.local.entity.CaptureLogEntity
import com.finanzas.automatica.presentation.ui.components.EmptyState
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.theme.ExpenseRose
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.WarningAmber

/**
 * Diagnóstico de captura: qué notificaciones bancarias llegaron y qué se hizo con cada
 * una.
 *
 * Responde la pregunta que antes solo se podía contestar con el teléfono conectado a
 * logcat — "llegó la notificación y no quedó el movimiento, ¿por qué?" — y deja copiar el
 * texto exacto, que es lo que hace falta para convertir una notificación que falló en un
 * caso de prueba real.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CaptureLogScreen(
    entries: List<CaptureLogEntity> = emptyList(),
    onlyIgnored: Boolean = false,
    onOnlyIgnoredChange: (Boolean) -> Unit = {},
    onClear: () -> Unit = {},
    onOpenMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    var confirmClear by remember { mutableStateOf(false) }

    val visible = if (onlyIgnored) {
        entries.filter { it.outcome != CaptureLogEntity.OUTCOME_CAPTURED }
    } else {
        entries
    }
    val ignoredCount = entries.count { it.outcome != CaptureLogEntity.OUTCOME_CAPTURED }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Diagnóstico de captura",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = when {
                            entries.isEmpty() -> "Sin notificaciones bancarias todavía"
                            ignoredCount == 0 -> "${entries.size} leídas, todas registradas"
                            else -> "${entries.size} leídas · $ignoredCount sin registrar"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            navigationIcon = {
                onOpenMenu?.let {
                    IconButton(onClick = it) {
                        Icon(Icons.Outlined.Menu, contentDescription = "Menu")
                    }
                }
            },
            actions = {
                if (entries.isNotEmpty()) {
                    IconButton(onClick = { confirmClear = true }) {
                        Icon(Icons.Outlined.DeleteSweep, contentDescription = "Borrar el registro")
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        if (entries.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Troubleshoot,
                title = "Nada que diagnosticar",
                message = "Cuando llegue una notificación de tu banco aparecerá aquí, con lo " +
                    "que Kivo entendió de ella o el motivo por el que no la registró. " +
                    "Este registro es solo tuyo: no sale del teléfono.",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !onlyIgnored,
                    onClick = { onOnlyIgnoredChange(false) },
                    label = { Text("Todas") }
                )
                FilterChip(
                    selected = onlyIgnored,
                    onClick = { onOnlyIgnoredChange(true) },
                    label = { Text("Sin registrar ($ignoredCount)") }
                )
            }

            if (visible.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.CheckCircle,
                    title = "Ninguna quedó por fuera",
                    message = "Todas las notificaciones bancarias que llegaron se convirtieron en movimientos.",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visible, key = { it.id }) { entry ->
                        CaptureLogItem(
                            entry = entry,
                            onCopy = { clipboard.setText(AnnotatedString(entry.rawText)) },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("¿Borrar el registro?") },
            text = {
                Text(
                    "Se borran las notificaciones guardadas para diagnóstico. Los movimientos " +
                        "que ya se registraron no se tocan."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmClear = false
                    onClear()
                }) { Text("Borrar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun CaptureLogItem(
    entry: CaptureLogEntity,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val (icon, tint) = outcomeVisuals(entry.outcome)

    FinanceCard(
        modifier = modifier,
        onClick = { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconBadge(
                icon = icon,
                contentDescription = null,
                tint = tint,
                containerColor = tint.copy(alpha = 0.12f)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.bankEntity?.lowercase()?.replaceFirstChar { it.uppercase() }
                            ?: entry.packageName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    FinanceTag(
                        text = relativeTimeShort(entry.createdAt),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Text(
                    text = entry.movementSummary ?: entry.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (entry.outcome == CaptureLogEntity.OUTCOME_CAPTURED) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(top = 2.dp)
                )

                // El texto crudo es el dato que hace falta para arreglar una regla, así
                // que se muestra completo (no recortado) al desplegar la tarjeta.
                Text(
                    text = entry.rawText,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    modifier = Modifier.padding(top = 6.dp)
                )

                if (expanded) {
                    Text(
                        text = entry.packageName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    TextButton(onClick = onCopy) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Copiar el texto",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun outcomeVisuals(outcome: String): Pair<ImageVector, Color> = when (outcome) {
    CaptureLogEntity.OUTCOME_CAPTURED -> Icons.Outlined.CheckCircle to IncomeGreen
    CaptureLogEntity.OUTCOME_FAILED -> Icons.Outlined.ErrorOutline to ExpenseRose
    else -> Icons.Outlined.RemoveCircleOutline to WarningAmber
}

private fun relativeTimeShort(timestamp: Long): String {
    val minutes = (System.currentTimeMillis() - timestamp) / 60_000
    return when {
        minutes < 1 -> "ahora"
        minutes < 60 -> "$minutes min"
        minutes < 1440 -> "${minutes / 60} h"
        else -> "${minutes / 1440} d"
    }
}
