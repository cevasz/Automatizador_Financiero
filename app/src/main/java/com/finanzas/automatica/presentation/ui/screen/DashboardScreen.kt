package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.annotation.OptIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onMenuClick: () -> Unit = {},
    onPendingClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("Finanzas Automática") },
            navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Outlined.Menu, contentDescription = "Menú") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        )

        // Resumen rápido
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard("Ingresos", "$ 2.450.000", TrendingUp, Color.Green, "Este mes")
            SummaryCard("Gastos", "$ 1.820.000", TrendingDown, Color.Red, "Este mes")
            SummaryCard("Balance", "$ 630.000", AccountBalanceWallet, Color.Blue, "Ahorro neto")
        }

        // Pendientes de confirmación
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onPendingClick
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.PendingActions, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.size(8.dp))
                        Text("Por confirmar (3)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Icon(Icons.Outlined.ChevronRight, contentDescription = "")
                }
                
                // Items pendientes
                PendingItem("Supermercado La 14", "Nequi • $ 85.000 • Hoy", true)
                PendingItem("Uber - Carrera", "Bancolombia • $ 25.000 • Ayer", false)
                PendingItem("Transferencia a Juan", "Daviplata • $ 50.000 • Ayer", false)
            }
        }

        // Últimos movimientos confirmados
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Últimos movimientos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        
        MovementItem("Éxito", "Supermercado", "Nequi", "-$ 85.000", false, true)
        MovementItem("Salario Enero", "Empresa ABC", "Bancolombia", "+$ 2.500.000", true, true)
        MovementItem("Uber", "Transporte", "Bancolombia", "-$ 25.000", false, true)
        MovementItem("Netflix", "Suscripción", "Nu", "-$ 29.900", false, true)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryCard(title: String, amount: String, icon: ImageVector, color: Color, subtitle: String) {
    Card(
        modifier = Modifier.weight(1f).height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(icon, contentDescription = "", tint = color)
            }
            Text(amount, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingItem(merchant: String, detail: String, isFirst: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(if (isFirst) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(merchant, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(detail, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Text("Confirmar", fontSize = 12.sp)
            }
            IconButton(onClick = { }) {
                Icon(Icons.Outlined.Close, contentDescription = "Rechazar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun MovementItem(
    merchant: String,
    category: String,
    bank: String,
    amount: String,
    isIncome: Boolean,
    showBank: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(merchant, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            if (showBank) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(category, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.size(8.dp))
                    Text("·", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.size(8.dp))
                    Text(bank, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Text(
            amount,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isIncome) Color.Green else MaterialTheme.colorScheme.onSurface
        )
    }
}