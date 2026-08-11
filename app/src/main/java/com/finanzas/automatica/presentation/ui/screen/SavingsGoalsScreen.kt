package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.domain.model.SavingsGoal
import com.finanzas.automatica.presentation.ui.components.EmptyState
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.InfoBlue
import com.finanzas.automatica.presentation.ui.theme.WarningAmber
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsGoalsScreen(
    goals: List<SavingsGoal>,
    onGoalClick: (SavingsGoal) -> Unit = {},
    onAddGoal: () -> Unit = {},
    onAddProgress: (Long, Long) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }
    }
    val totalTarget = goals.sumOf { it.targetAmount }
    val totalSaved = goals.sumOf { it.currentAmount }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Metas de ahorro",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = currencyFormat.money(totalSaved) + " de " + currencyFormat.money(totalTarget),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                IconButton(onClick = onAddGoal) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Agregar meta"
                    )
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
            if (goals.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.Savings,
                        title = "Sin metas de ahorro",
                        message = "Crea metas para separar objetivos concretos y medir el avance mes a mes.",
                        actionLabel = "Crear meta",
                        onAction = onAddGoal
                    )
                }
            } else {
                items(goals, key = { it.id ?: it.name }) { goal ->
                    SavingsGoalCard(
                        goal = goal,
                        currencyFormat = currencyFormat,
                        onClick = { onGoalClick(goal) }
                    )
                }
            }
        }
    }
}

@Composable
fun SavingsGoalCard(
    goal: SavingsGoal,
    currencyFormat: NumberFormat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (goal.targetAmount > 0) {
        (goal.currentAmount.toFloat() / goal.targetAmount.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val isCompleted = goal.currentAmount >= goal.targetAmount

    FinanceCard(
        modifier = modifier.clickable(onClick = onClick),
        containerColor = if (isCompleted) IncomeGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
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
                    icon = Icons.Outlined.Flag,
                    contentDescription = goal.name,
                    tint = if (isCompleted) IncomeGreen else InfoBlue
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = goal.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Hasta " + goal.targetDateLabel(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            FinanceTag(
                text = if (isCompleted) "Completada" else "${(progress * 100).toInt()}%",
                color = if (isCompleted) IncomeGreen else WarningAmber,
                containerColor = if (isCompleted) IncomeGreen.copy(alpha = 0.12f) else WarningAmber.copy(alpha = 0.12f)
            )
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = if (isCompleted) IncomeGreen else InfoBlue,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currencyFormat.money(goal.currentAmount),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = currencyFormat.money(goal.targetAmount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = if (isCompleted) {
                "Objetivo alcanzado"
            } else {
                "Faltan " + currencyFormat.money((goal.targetAmount - goal.currentAmount).coerceAtLeast(0))
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun NumberFormat.money(amount: Long): String = format(amount / 100.0)

private fun SavingsGoal.targetDateLabel(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "CO"))
        .withZone(ZoneId.systemDefault())
    return formatter.format(targetDate)
}
