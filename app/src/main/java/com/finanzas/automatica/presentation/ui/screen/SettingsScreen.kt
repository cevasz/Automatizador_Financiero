package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.components.SectionHeader
import com.finanzas.automatica.presentation.ui.theme.ExpenseRose
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.InfoBlue
import com.finanzas.automatica.presentation.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    notificationAccessEnabled: Boolean = false,
    autoConfirmHighConfidence: Boolean = true,
    biometricEnabled: Boolean = false,
    exportDataFormat: String = "CSV",
    onEnableNotificationAccess: () -> Unit = {},
    onAutoConfirmChange: (Boolean) -> Unit = {},
    onBiometricChange: (Boolean) -> Unit = {},
    onExportFormatChange: (String) -> Unit = {},
    onExportData: () -> Unit = {},
    onDeleteData: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onOpenMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Ajustes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Privacidad, captura y datos locales",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader(title = "Captura automatica")
            }
            item {
                NotificationAccessRow(
                    enabled = notificationAccessEnabled,
                    onEnable = onEnableNotificationAccess
                )
            }
            item {
                SettingRow(
                    icon = Icons.Outlined.CheckCircle,
                    iconTint = WarningAmber,
                    title = "Confirmacion automatica",
                    subtitle = "Aceptar movimientos con comercio y categoria confiables",
                    trailing = {
                        Switch(
                            checked = autoConfirmHighConfidence,
                            onCheckedChange = onAutoConfirmChange
                        )
                    }
                )
            }

            item {
                SectionHeader(title = "Seguridad")
            }
            item {
                SettingRow(
                    icon = Icons.Outlined.Lock,
                    iconTint = MaterialTheme.colorScheme.secondary,
                    title = "Biometria",
                    subtitle = "Bloquear la app con huella o desbloqueo facial",
                    trailing = {
                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = onBiometricChange
                        )
                    }
                )
            }

            item {
                SectionHeader(title = "Apariencia")
            }
            item {
                SettingRow(
                    icon = Icons.Outlined.Palette,
                    iconTint = MaterialTheme.colorScheme.primary,
                    title = "Tema visual",
                    subtitle = "Claro pastel profesional",
                    trailing = {
                        Text(
                            text = "Activo",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            item {
                SectionHeader(title = "Datos")
            }
            item {
                SettingRow(
                    icon = Icons.Outlined.Sync,
                    iconTint = MaterialTheme.colorScheme.primary,
                    title = "Cuenta y sincronizacion",
                    subtitle = "Conectar el panel web y preparar el envio de datos",
                    trailing = {
                        Icon(
                            imageVector = Icons.Outlined.OpenInNew,
                            contentDescription = "Abrir cuenta"
                        )
                    },
                    onClick = onAccountClick
                )
            }
            item {
                FinanceCard {
                    Text(
                        text = "Formato de exportacion",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val exportFormats = listOf("CSV", "Excel", "PDF")
                        exportFormats.forEach { format: String ->
                            FilterChip(
                                selected = exportDataFormat == format,
                                onClick = { onExportFormatChange(format) },
                                label = { Text(format) }
                            )
                        }
                    }
                }
            }
            item {
                SettingRow(
                    icon = Icons.Outlined.Download,
                    iconTint = InfoBlue,
                    title = "Exportar datos",
                    subtitle = "Preparar movimientos en $exportDataFormat",
                    trailing = {
                        Icon(
                            imageVector = Icons.Outlined.OpenInNew,
                            contentDescription = "Exportar"
                        )
                    },
                    onClick = onExportData
                )
            }
            item {
                SettingRow(
                    icon = Icons.Outlined.DeleteForever,
                    iconTint = ExpenseRose,
                    title = "Eliminar todos los datos",
                    subtitle = "Borrar historial, agenda, metas y presupuestos",
                    trailing = {
                        Icon(
                            imageVector = Icons.Outlined.DeleteForever,
                            contentDescription = "Eliminar",
                            tint = ExpenseRose
                        )
                    },
                    onClick = onDeleteData
                )
            }

            item {
                SectionHeader(title = "Acerca de")
            }
            item {
                SettingRow(
                    icon = Icons.Outlined.Info,
                    iconTint = MaterialTheme.colorScheme.primary,
                    title = "Version",
                    subtitle = "1.0.0",
                    trailing = { }
                )
            }
        }
    }
}

@Composable
private fun NotificationAccessRow(
    enabled: Boolean,
    onEnable: () -> Unit
) {
    FinanceCard(
        containerColor = if (!enabled) WarningAmber.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
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
                    icon = Icons.Outlined.Notifications,
                    contentDescription = "Acceso a notificaciones",
                    tint = if (enabled) IncomeGreen else WarningAmber
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "Captura de notificaciones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (enabled) {
                            "Activo: la app registra movimientos desde SMS, Gmail y apps de bancos."
                        } else {
                            "Desactivado. Activa el acceso una sola vez en los ajustes de Android para que la app pueda leer SMS, Gmail y notificaciones bancarias."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(modifier = Modifier.padding(start = 12.dp)) {
                if (enabled) {
                    FinanceTag(
                        text = "Activo",
                        color = IncomeGreen,
                        containerColor = IncomeGreen.copy(alpha = 0.12f)
                    )
                } else {
                    Button(onClick = onEnable) {
                        Text("Habilitar")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    FinanceCard(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
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
                    icon = icon,
                    contentDescription = title,
                    tint = iconTint
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier.padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                trailing()
            }
        }
    }
}
