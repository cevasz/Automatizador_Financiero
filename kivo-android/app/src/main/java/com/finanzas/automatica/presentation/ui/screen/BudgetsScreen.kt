package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.domain.model.Budget
import com.finanzas.automatica.domain.model.Category
import com.finanzas.automatica.presentation.ui.components.AnimatedAmountText
import com.finanzas.automatica.presentation.ui.components.EmptyState
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.components.pressFeedback
import com.finanzas.automatica.presentation.ui.components.rememberAnimatedFloat
import com.finanzas.automatica.presentation.viewmodel.budgetKey
import com.finanzas.automatica.presentation.ui.theme.ExpenseRose
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.InfoBlue
import com.finanzas.automatica.presentation.ui.theme.WarningAmber
import java.text.NumberFormat
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BudgetsScreen(
    budgets: List<Budget>,
    categories: List<Category>,
    spentByBudgetKey: Map<String, Long> = emptyMap(),
    onBudgetClick: (Budget) -> Unit = {},
    onAddBudget: () -> Unit = {},
    onOpenMenu: (() -> Unit)? = null,
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
                items(budgets, key = { budgetKey(it) }) { budget ->
                    val category = categories.find { it.id == budget.categoryId }
                    BudgetCard(
                        budget = budget,
                        category = category,
                        spent = spentByBudgetKey[budgetKey(budget)] ?: 0L,
                        currencyFormat = currencyFormat,
                        onClick = { onBudgetClick(budget) },
                        modifier = Modifier.animateItemPlacement()
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
) {    val limit = budget.monthlyLimit
    val progress = if (limit > 0) (spent.toFloat() / limit.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress = rememberAnimatedFloat(progress)
    val isOverBudget = spent > limit
    val progressColor = when {
        isOverBudget -> ExpenseRose
        progress > 0.82f -> WarningAmber
        else -> IncomeGreen
    }

    FinanceCard(
        modifier = modifier.pressFeedback(onClick = onClick),
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
                progress = animatedProgress,
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
                AnimatedAmountText(
                    target = spent,
                    format = { currencyFormat.money(it) },
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

/**
 * Crear/editar un presupuesto. Antes onAddBudget/onBudgetClick eran no-ops en
 * BudgetsScreen -- este formulario es lo que faltaba para que ambos botones
 * funcionen. El mes/año se fija al mes actual (un presupuesto siempre aplica al
 * mes vigente, ver BudgetsViewModel.warnIfAlreadyOverLimit); solo son editables
 * la categoría y el límite mensual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBudgetScreen(
    budget: Budget?,
    categories: List<Category>,
    onSave: (Budget) -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedCategoryId by rememberSaveable { mutableStateOf(budget?.categoryId) }
    var limitInput by rememberSaveable {
        mutableStateOf(if (budget != null) (budget.monthlyLimit / 100L).toString() else "")
    }
    val today = remember { LocalDate.now() }
    val limitCents = limitInput.toLongOrNull()?.let { it * 100 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text(if (budget == null) "Nuevo presupuesto" else "Editar presupuesto") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                if (categories.isEmpty()) {
                    Text(
                        text = "Crea una categoría de gasto primero.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategoryId == category.id,
                                onClick = { selectedCategoryId = category.id },
                                label = { Text(category.name) }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = limitInput,
                onValueChange = { value -> if (value.all { it.isDigit() }) limitInput = value },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Límite mensual (COP)") },
                prefix = { Text("$ ") }
            )

            Text(
                text = "Aplica a " + today.month
                    .getDisplayName(TextStyle.FULL, Locale("es", "CO"))
                    .replaceFirstChar { it.titlecase(Locale("es", "CO")) } + " ${today.year}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        val categoryId = selectedCategoryId ?: return@Button
                        val limit = limitCents ?: return@Button
                        onSave(
                            Budget(
                                id = budget?.id,
                                categoryId = categoryId,
                                monthlyLimit = limit,
                                month = budget?.month ?: today.monthValue,
                                year = budget?.year ?: today.year,
                                createdAt = budget?.createdAt ?: java.time.Instant.now()
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedCategoryId != null && limitCents != null && limitCents > 0,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Guardar")
                }
            }

            if (onDelete != null) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Eliminar presupuesto")
                }
            }
        }
    }
}

private fun NumberFormat.money(amount: Long): String = format(amount / 100.0)

private fun Budget.periodLabel(): String {
    val monthName = Month.of(month)
        .getDisplayName(TextStyle.FULL, Locale("es", "CO"))
        .replaceFirstChar { it.titlecase(Locale("es", "CO")) }
    return "$monthName $year"
}
