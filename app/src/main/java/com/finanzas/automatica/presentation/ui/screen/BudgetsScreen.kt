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
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.PieChart
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
import com.finanzas.automatica.domain.model.Budget
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.presentation.ui.components.EmptyState
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.theme.ExpenseRose
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.InfoBlue
import com.finanzas.automatica.presentation.ui.theme.WarningAmber
import java.text.NumberFormat
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(
    budgets: List<Budget>,
    categories: List<Category>,
    onBudgetClick: (Budget) -> Unit = {},
    onAddBudget: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }
    }
    val totalPlanned = budgets.sumOf { it.monthlyLimit }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Planes de gasto",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = currencyFormat.money(totalPlanned) + " presupuestados",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                IconButton(onClick = onAddBudget) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Agregar presupuesto"
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
            if (budgets.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        title = "Sin planes de gasto",
                        message = "Crea presupuestos por categoria para comparar el gasto del mes contra tus limites.",
                        actionLabel = "Crear presupuesto",
                        onAction = onAddBudget
                    )
                }
            } else {
                items(budgets, key = { it.id ?: "${it.categoryId}-${it.month}-${it.year}" }) { budget ->
                    val category = categories.find { it.id == budget.categoryId }
                    BudgetCard(
                        budget = budget,
                        category = category,
                        spent = 0L,
                        currencyFormat = currencyFormat,
                        onClick = { onBudgetClick(budget) }
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetCard(
    budget: Budget,
    category: Category?,
    spent: Long,
    currencyFormat: NumberFormat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val limit = budget.monthlyLimit
    val progress = if (limit > 0) (spent.toFloat() / limit.toFloat()).coerceIn(0f, 1f) else 0f
    val isOverBudget = spent > limit
    val progressColor = when {
        isOverBudget -> ExpenseRose
        progress > 0.82f -> WarningAmber
        else -> IncomeGreen
    }

    FinanceCard(
        modifier = modifier.clickable(onClick = onClick),
        containerColor = if (isOverBudget) ExpenseRose.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
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
                    icon = Icons.Outlined.PieChart,
                    contentDescription = category?.name ?: "Presupuesto",
                    tint = if (isOverBudget) ExpenseRose else InfoBlue
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = category?.name ?: "Sin categoria",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = budget.periodLabel(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            FinanceTag(
                text = if (isOverBudget) "Excedido" else "Activo",
                color = if (isOverBudget) ExpenseRose else IncomeGreen,
                containerColor = if (isOverBudget) ExpenseRose.copy(alpha = 0.12f) else IncomeGreen.copy(alpha = 0.12f)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currencyFormat.money(spent),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = currencyFormat.money(limit),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = if (isOverBudget) {
                currencyFormat.money(spent - limit) + " por encima del limite"
            } else {
                currencyFormat.money((limit - spent).coerceAtLeast(0)) + " disponibles"
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (isOverBudget) ExpenseRose else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BudgetDetailScreen(
    budget: Budget,
    category: Category?,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }
    }
    FinanceCard(modifier = modifier.padding(16.dp)) {
        Text(
            text = category?.name ?: "Presupuesto",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = currencyFormat.money(budget.monthlyLimit),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun NumberFormat.money(amount: Long): String = format(amount / 100.0)

private fun Budget.periodLabel(): String {
    val monthName = Month.of(month)
        .getDisplayName(TextStyle.FULL, Locale("es", "CO"))
        .replaceFirstChar { it.titlecase(Locale("es", "CO")) }
    return "$monthName $year"
}
