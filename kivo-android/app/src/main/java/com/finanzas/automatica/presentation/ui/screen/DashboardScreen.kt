package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.domain.model.ConfirmationState
import com.finanzas.automatica.domain.model.Movement
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.presentation.ui.components.AnimatedAmountText
import com.finanzas.automatica.presentation.ui.components.EmptyState
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.components.SectionHeader
import com.finanzas.automatica.presentation.ui.components.appearFromBelow
import com.finanzas.automatica.presentation.ui.components.rememberAnimatedFloat
import com.finanzas.automatica.presentation.ui.theme.ExpenseRose
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.InfoBlue
import com.finanzas.automatica.presentation.ui.theme.WarningAmber
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class DashboardPeriod(val label: String) {
    Month("Mes"),
    Week("Semana"),
    Day("Hoy")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    movements: List<Movement> = emptyList(),
    pendingCount: Int = 0,
    notificationAccessEnabled: Boolean = true,
    onEnableNotificationAccess: () -> Unit = {},
    onPendingClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onOpenMenu: (() -> Unit)? = null
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }
    }
    var selectedPeriod by remember { mutableStateOf(DashboardPeriod.Month) }
    val visibleMovements = remember(movements) {
        movements
            .filterNot { it.confirmationState == ConfirmationState.REJECTED }
            .sortedByDescending { it.date }
    }
    val income = visibleMovements
        .filter { it.type == MovementType.INCOME }
        .sumOf { it.amount }
    val expenses = visibleMovements
        .filter { it.type == MovementType.EXPENSE }
        .sumOf { it.amount }
    val balance = income - expenses
    val pendingMovements = visibleMovements
        .filter { it.confirmationState == ConfirmationState.PENDING }
        .take(3)
    val recentMovements = visibleMovements.take(5)
    val spendingRatio = if (income > 0) (expenses.toFloat() / income.toFloat()).coerceIn(0f, 1f) else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Kivo",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            FinanceTag(
                                text = "Tu dinero, en orden",
                                color = WarningAmber,
                                containerColor = WarningAmber.copy(alpha = 0.15f)
                            )
                        }
                        Text(
                            text = "Hola, Kivo está listo para ayudarte con tu control financiero",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Ajustes"
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
        }

        item {
            AnimatedVisibility(
                visible = !notificationAccessEnabled,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                FinanceCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    containerColor = WarningAmber.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconBadge(
                            icon = Icons.Outlined.Notifications,
                            contentDescription = "Captura desactivada",
                            tint = WarningAmber
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Captura automatica desactivada",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "La app no puede leer SMS, Gmail ni notificaciones bancarias. Activa el acceso una sola vez para que los movimientos se registren solos.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = onEnableNotificationAccess) {
                            Text("Activar", color = WarningAmber)
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DashboardPeriod.entries.forEach { period ->
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = { selectedPeriod = period },
                        label = { Text(period.label) }
                    )
                }
            }
        }

        item {
            FinanceCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .appearFromBelow(delayMillis = 0),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Balance neto",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        AnimatedAmountText(
                            target = balance,
                            format = { currencyFormat.money(it) },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconBadge(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = "Balance",
                        tint = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }
                LinearProgressIndicator(
                    progress = rememberAnimatedFloat(spendingRatio),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (spendingRatio > 0.85f) WarningAmber else IncomeGreen,
                    trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                )
                Text(
                    text = "${(spendingRatio * 100).toInt()}% de ingresos comprometidos en gastos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .appearFromBelow(delayMillis = 60),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Ingresos",
                    amount = income,
                    currencyFormat = currencyFormat,
                    icon = Icons.Outlined.TrendingUp,
                    tint = IncomeGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Gastos",
                    amount = expenses,
                    currencyFormat = currencyFormat,
                    icon = Icons.Outlined.TrendingDown,
                    tint = ExpenseRose,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Pendientes",
                    amount = pendingCount.toLong(),
                    currencyFormat = currencyFormat,
                    icon = Icons.Outlined.PendingActions,
                    tint = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionHeader(
                title = "Por confirmar",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .appearFromBelow(delayMillis = 120),
                action = {
                    TextButton(onClick = onPendingClick) {
                        Text("Ver")
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null
                        )
                    }
                }
            )
        }

        item {
            FinanceCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .appearFromBelow(delayMillis = 160)
            ) {
                AnimatedContent(
                    targetState = pendingMovements.isEmpty(),
                    transitionSpec = {
                        (fadeIn() togetherWith fadeOut()).using(SizeTransform(clip = false))
                    }
                ) { isEmpty ->
                    if (isEmpty) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconBadge(
                                icon = Icons.Outlined.CheckCircle,
                                contentDescription = "Sin pendientes",
                                tint = IncomeGreen
                            )
                            Column {
                                Text(
                                    text = "Todo esta confirmado",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Los movimientos nuevos apareceran aqui cuando requieran revision.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            pendingMovements.forEach { movement ->
                                PendingMovementRow(
                                    movement = movement,
                                    currencyFormat = currencyFormat
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Ultimos movimientos",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .appearFromBelow(delayMillis = 200)
            )
        }

        if (recentMovements.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.Notifications,
                    title = "Sin movimientos registrados",
                    message = "Cuando actives el acceso a notificaciones, la app empezara a crear tu historial financiero automaticamente.",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else {
            items(recentMovements.size, key = { index -> recentMovements[index].id }) { index ->
                MovementLine(
                    movement = recentMovements[index],
                    currencyFormat = currencyFormat,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .animateItemPlacement()
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    amount: Long,
    currencyFormat: NumberFormat,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    FinanceCard(
        modifier = modifier,
        containerColor = tint.copy(alpha = 0.1f)
    ) {
        IconBadge(
            icon = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier.align(Alignment.Start)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            AnimatedAmountText(
                target = amount,
                format = { value -> if (title == "Pendientes") value.toString() else currencyFormat.money(value) },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PendingMovementRow(
    movement: Movement,
    currencyFormat: NumberFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = movement.counterpartyRaw,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${movement.bankEntity.name.cleanEnum()} / ${movement.shortDate()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = movement.signedAmount(currencyFormat),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (movement.type == MovementType.INCOME) IncomeGreen else ExpenseRose
            )
            FinanceTag(
                text = "Revision",
                color = WarningAmber,
                containerColor = WarningAmber.copy(alpha = 0.12f)
            )
        }
    }
}

@Composable
private fun MovementLine(
    movement: Movement,
    currencyFormat: NumberFormat,
    modifier: Modifier = Modifier
) {
    FinanceCard(modifier = modifier) {
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
                    icon = if (movement.type == MovementType.INCOME) Icons.Outlined.TrendingUp else Icons.Outlined.TrendingDown,
                    contentDescription = movement.type.name,
                    tint = if (movement.type == MovementType.INCOME) IncomeGreen else InfoBlue
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = movement.counterpartyRaw,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${movement.paymentMethod.name.cleanEnum()} / ${movement.shortDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.padding(horizontal = 4.dp))
            Text(
                text = movement.signedAmount(currencyFormat),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (movement.type == MovementType.INCOME) IncomeGreen else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun NumberFormat.money(amount: Long): String = format(amount / 100.0)

private fun Movement.signedAmount(currencyFormat: NumberFormat): String {
    val prefix = if (type == MovementType.INCOME) "+" else "-"
    return "$prefix${currencyFormat.money(amount)}"
}

private fun Movement.shortDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale("es", "CO"))
        .withZone(ZoneId.systemDefault())
    return formatter.format(date)
}

private fun String.cleanEnum(): String {
    return lowercase()
        .replace('_', ' ')
        .replaceFirstChar { it.titlecase(Locale("es", "CO")) }
}
