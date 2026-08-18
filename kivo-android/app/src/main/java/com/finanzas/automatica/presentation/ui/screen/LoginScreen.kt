package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.finanzas.automatica.R
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.components.appearFromBelow
import com.finanzas.automatica.presentation.ui.theme.ExpenseRose
import com.finanzas.automatica.presentation.ui.theme.InfoBlue
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.WarningAmber

/**
 * Cuenta de Kivo y sincronizacion.
 *
 * Antes esta pantalla pedia "URL del backend" y "token de acceso" escritos a
 * mano: nadie fuera del proyecto podia rellenar eso, y el boton "Sincronizar"
 * en realidad solo guardaba un JSON dentro del propio telefono. Ahora es correo
 * y contraseña, y la sincronizacion sube y baja de verdad contra Supabase.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    isConfigured: Boolean,
    isConnected: Boolean,
    email: String,
    lastSyncLabel: String,
    syncing: Boolean,
    uploadRawText: Boolean,
    message: String?,
    error: String?,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit,
    onSignOut: () -> Unit,
    onSyncNow: () -> Unit,
    onUploadRawTextChange: (Boolean) -> Unit,
    onOpenMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var emailInput by rememberSaveable { mutableStateOf(email) }
    var passwordInput by rememberSaveable { mutableStateOf("") }
    var creandoCuenta by rememberSaveable { mutableStateOf(false) }

    val credencialesListas = emailInput.contains('@') && passwordInput.length >= 8

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
                        Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Menu")
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
            Image(
                painter = painterResource(R.drawable.onboarding_security),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .appearFromBelow(),
                contentScale = ContentScale.Crop
            )

            // Sin credenciales de Supabase la sincronizacion no existe en esta
            // compilacion. Se dice de frente en vez de dejar botones que fallan.
            if (!isConfigured) {
                FinanceCard(containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
                    Text(
                        text = "Sincronizacion no configurada",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Esta version de la app se compilo sin datos de servidor, asi que todo " +
                            "funciona solo en este telefono. Kivo esta pensado para funcionar asi: la " +
                            "nube es opcional.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            FinanceCard(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.appearFromBelow(delayMillis = 80)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                "Tus movimientos se sincronizan con el panel web cuando lo pidas."
                            } else {
                                "Crea una cuenta de Kivo (no es la de tu banco) para ver tus " +
                                    "movimientos tambien desde el computador."
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FinanceTag(
                        text = if (isConnected) "Conectado" else "Desconectado",
                        color = if (isConnected) IncomeGreen else WarningAmber,
                        containerColor = if (isConnected) {
                            IncomeGreen.copy(alpha = 0.12f)
                        } else {
                            WarningAmber.copy(alpha = 0.12f)
                        }
                    )
                    FinanceTag(
                        text = lastSyncLabel,
                        color = InfoBlue,
                        containerColor = InfoBlue.copy(alpha = 0.12f)
                    )
                }
            }

            AnimatedVisibility(visible = message != null || error != null) {
                FinanceCard {
                    Text(
                        text = error ?: message.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (error != null) ExpenseRose else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Crossfade(targetState = isConnected, label = "loginConnectionState") { conectado ->
                if (conectado) {
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
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = onSyncNow, enabled = !syncing) {
                                if (syncing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Icon(Icons.Outlined.CloudSync, contentDescription = null)
                                }
                                Spacer(Modifier.padding(horizontal = 4.dp))
                                Text(if (syncing) "Sincronizando" else "Sincronizar")
                            }
                            TextButton(onClick = onSignOut, enabled = !syncing) {
                                Icon(Icons.Outlined.Logout, contentDescription = null)
                                Spacer(Modifier.padding(horizontal = 4.dp))
                                Text("Cerrar sesion")
                            }
                        }
                    }
                } else {
                    FinanceCard(modifier = Modifier.appearFromBelow(delayMillis = 60)) {
                        Text(
                            text = if (creandoCuenta) "Crear cuenta de Kivo" else "Entrar a tu cuenta",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Correo") },
                            singleLine = true,
                            enabled = isConfigured && !syncing,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Contraseña") },
                            singleLine = true,
                            enabled = isConfigured && !syncing,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            supportingText = { Text("Minimo 8 caracteres") }
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (creandoCuenta) onSignUp(emailInput, passwordInput)
                                else onSignIn(emailInput, passwordInput)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isConfigured && credencialesListas && !syncing,
                            contentPadding = PaddingValues(vertical = 14.dp)
                        ) {
                            Icon(Icons.Outlined.Login, contentDescription = null)
                            Spacer(Modifier.padding(horizontal = 4.dp))
                            Text(if (creandoCuenta) "Crear cuenta" else "Entrar")
                        }
                        OutlinedButton(
                            onClick = { creandoCuenta = !creandoCuenta },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isConfigured && !syncing
                        ) {
                            Text(
                                if (creandoCuenta) "Ya tengo cuenta, entrar"
                                else "No tengo cuenta, crear una"
                            )
                        }
                    }
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
                            text = "Esta cuenta es solo de Kivo y sirve para ver tus propios datos " +
                                "desde el computador. Tu banco no interviene.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.padding(end = 12.dp)) {
                        Text(
                            text = "Subir el texto original del banco",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Es lo que permite al panel web explicar de donde salio cada " +
                                "movimiento. Si lo apagas, el movimiento se sincroniza igual pero sin " +
                                "el texto de la notificacion.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = uploadRawText, onCheckedChange = onUploadRawTextChange)
                }
            }
        }
    }
}
