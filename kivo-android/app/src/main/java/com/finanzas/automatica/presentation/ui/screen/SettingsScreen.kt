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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.outlined.Warning
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
import com.finanzas.automatica.presentation.ui.theme.AppThemePalette
import com.finanzas.automatica.presentation.ui.theme.ExpenseRose
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.InfoBlue
import com.finanzas.automatica.presentation.ui.theme.ThemeMode
import com.finanzas.automatica.presentation.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    notificationAccessEnabled: Boolean = false,
    autoConfirmHighConfidence: Boolean = true,
    biometricEnabled: Boolean = false,
    isBiometricAvailable: Boolean = true,
    exportDataFormat: String = "CSV",
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    themePalette: AppThemePalette = AppThemePalette.KIVO_CORAL,
    onEnableNotificationAccess: () -> Unit = {},
    onAutoConfirmChange: (Boolean) -> Unit = {},
    onBiometricChange: (Boolean) -> Unit = {},
    onExportFormatChange: (String) -> Unit = {},
    onExportData: () -> Unit = {},
    onDeleteData: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onThemePaletteChange: (AppThemePalette) -> Unit = {},
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
            if (biometricEnabled) {
                item {
                    if (isBiometricAvailable) {
                        Text(
                            text = "Protegido con huella digital, reconocimiento facial o PIN del dispositivo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        FinanceCard(
                            containerColor = WarningAmber.copy(alpha = 0.08f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconBadge(
                                    icon = Icons.Outlined.Warning,
                                    contentDescription = "Warning",
                                    tint = WarningAmber
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Biometría no disponible",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Configura primero una huella, rostro o PIN en los ajustes de tu dispositivo",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                SectionHeader(title = "Apariencia")
            }
            item {
                FinanceCard {
                    Text(
                        text = "Modo de tema",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeMode.values().forEach { mode ->
                            val label = when(mode) {
                                ThemeMode.LIGHT -> "Claro"
                                ThemeMode.DARK -> "Oscuro"
                                ThemeMode.SYSTEM -> "Sistema"
                            }
                            FilterChip(
                                selected = themeMode == mode,
                                onClick = { onThemeModeChange(mode) },
                                label = { Text(label) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Paleta de colores",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppThemePalette.values().forEach { palette ->
                            val isSelected = themePalette == palette
                            val colors = palettePreviewColors(palette)
                            val paletteName = when (palette) {
                                AppThemePalette.KIVO_CORAL -> "Kivo Coral"
                                AppThemePalette.OCEAN_TEAL -> "Oceano Teal"
                                AppThemePalette.FOREST_GREEN -> "Bosque Verde"
                                AppThemePalette.MIDNIGHT_BLUE -> "Medianoche"
                                AppThemePalette.SUNSET_AMBER -> "Atardecer"
                            }
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { onThemePaletteChange(palette) }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(modifier = Modifier.size(12.dp).background(colors.first, CircleShape))
                                        Box(modifier = Modifier.size(12.dp).background(colors.second, CircleShape))
                                        Box(modifier = Modifier.size(12.dp).background(colors.third, CircleShape))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = paletteName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
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

private fun palettePreviewColors(palette: AppThemePalette): Triple<Color, Color, Color> {
    return when (palette) {
        AppThemePalette.KIVO_CORAL -> Triple(Color(0xFFF56565), Color(0xFF2C7A7B), Color(0xFFD69E2E))
        AppThemePalette.OCEAN_TEAL -> Triple(Color(0xFF0891B2), Color(0xFF164E63), Color(0xFF22D3EE))
        AppThemePalette.FOREST_GREEN -> Triple(Color(0xFF059669), Color(0xFF064E3B), Color(0xFF10B981))
        AppThemePalette.MIDNIGHT_BLUE -> Triple(Color(0xFF3B82F6), Color(0xFF1E3A5F), Color(0xFF93C5FD))
        AppThemePalette.SUNSET_AMBER -> Triple(Color(0xFFF59E0B), Color(0xFF92400E), Color(0xFFFBBF24))
    }
}
