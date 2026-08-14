package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.components.appearFromBelow
import com.finanzas.automatica.presentation.ui.theme.InfoBlue
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    isConnected: Boolean,
    email: String,
    backendUrl: String,
    lastSyncLabel: String,
    onConnect: (String, String, String) -> Unit,
    onDisconnect: () -> Unit,
    onSyncNow: () -> Unit,
    onOpenMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var emailInput by rememberSaveable { mutableStateOf(email) }
    var backendUrlInput by rememberSaveable { mutableStateOf(backendUrl) }
    var accessTokenInput by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(email, backendUrl) {
        if (emailInput.isBlank()) emailInput = email
        if (backendUrlInput.isBlank()) backendUrlInput = backendUrl
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
                        text = "Cuenta y sincronizacion",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Conecta la app movil con el panel web",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FinanceCard(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.appearFromBelow()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconBadge(
                        icon = Icons.Outlined.CloudSync,
                        contentDescription = "Sincronizacion",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (isConnected) "Sesion activa" else "Sin sesion activa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isConnected) {
                                "Tus datos se preparan para sincronizarse con la app web."
                            } else {
                                "Inicia sesion con tu cuenta del panel web para habilitar la sincronizacion."
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FinanceTag(
                        text = if (isConnected) "Conectado" else "Desconectado",
                        color = if (isConnected) IncomeGreen else WarningAmber,
                        containerColor = if (isConnected) IncomeGreen.copy(alpha = 0.12f) else WarningAmber.copy(alpha = 0.12f)
                    )
                    FinanceTag(
                        text = lastSyncLabel,
                        color = InfoBlue,
                        containerColor = InfoBlue.copy(alpha = 0.12f)
                    )
                }
            }

            Crossfade(targetState = isConnected, label = "loginConnectionState") { connected ->
                if (connected) {
                    FinanceCard {
                        Text(
                            text = "Cuenta vinculada",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = email.ifBlank { "Cuenta sin correo guardado" },
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = backendUrl.ifBlank { "Sin URL de backend" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = onSyncNow) {
                                Icon(
                                    imageVector = Icons.Outlined.CloudSync,
                                    contentDescription = null
                                )
                                Spacer(Modifier.padding(horizontal = 4.dp))
                                Text("Sincronizar")
                            }
                            TextButton(onClick = onDisconnect) {
                                Icon(
                                    imageVector = Icons.Outlined.Logout,
                                    contentDescription = null
                                )
                                Spacer(Modifier.padding(horizontal = 4.dp))
                                Text("Cerrar sesion")
                            }
                        }
                    }
                } else {
                    FinanceCard {
                        Text(
                            text = "Conecta tu cuenta web",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Usa la misma cuenta del panel web para preparar la sincronizacion de movimientos, agenda y categorias.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            FinanceCard(modifier = Modifier.appearFromBelow(delayMillis = 60)) {
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo del panel web") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = backendUrlInput,
                    onValueChange = { backendUrlInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("URL del backend") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = accessTokenInput,
                    onValueChange = { accessTokenInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Token de acceso") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onConnect(emailInput, backendUrlInput, accessTokenInput) },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Login,
                        contentDescription = null
                    )
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text(if (isConnected) "Actualizar sesion" else "Iniciar sesion")
                }
            }

            FinanceCard(modifier = Modifier.appearFromBelow(delayMillis = 120)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconBadge(
                        icon = Icons.Outlined.Security,
                        contentDescription = "Privacidad",
                        tint = WarningAmber
                    )
                    Column {
                        Text(
                            text = "No pedimos credenciales bancarias",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "La sesion es solo para el panel web y la sincronizacion de tus propios datos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
