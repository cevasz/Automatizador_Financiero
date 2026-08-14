package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.R
import com.finanzas.automatica.domain.model.SavingsGoal
import com.finanzas.automatica.presentation.ui.components.AnimatedAmountText
import com.finanzas.automatica.presentation.ui.components.EmptyState
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.components.pressFeedback
import com.finanzas.automatica.presentation.ui.components.rememberAnimatedFloat
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.InfoBlue
import com.finanzas.automatica.presentation.ui.theme.WarningAmber
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SavingsGoalsScreen(
    goals: List<SavingsGoal>,
    onGoalClick: (SavingsGoal) -> Unit = {},
    onAddGoal: () -> Unit = {},
    onAddProgress: (Long, Long) -> Unit = { _, _ -> },
    onOpenMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }
    }
    val totalTarget = goals.sumOf { it.targetAmount }
    val totalSaved = goals.sumOf { it.currentAmount }

    // Momento "pico" (peak-end rule): cuando un abono hace que una meta cruce el 100%,
    // se muestra una celebracion en pantalla completa en vez de solo el aviso silencioso
    // que ya existia (notificacion del sistema en SavingsGoalsViewModel). El usuario
    // recuerda sobre todo el pico y el cierre de una experiencia -- este es el pico.
    var celebratingGoal by remember { mutableStateOf<SavingsGoal?>(null) }

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
            if (goals.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.Savings,
                        title = "Sin metas de ahorro",
                        message = "Crea metas para separar objetivos concretos y medir el avance mes a mes.",
                        actionLabel = "Crear meta",
                        onAction = onAddGoal,
                        illustrationRes = R.drawable.savings_goal_illustration
                    )
                }
            } else {
                items(goals, key = { it.id ?: it.name }) { goal ->
                    SavingsGoalCard(
                        goal = goal,
                        currencyFormat = currencyFormat,
                        onClick = { onGoalClick(goal) },
                        onAddProgress = { id, amount ->
                            val wasIncomplete = goal.currentAmount < goal.targetAmount
                            val willComplete = goal.currentAmount + amount >= goal.targetAmount
                            onAddProgress(id, amount)
                            if (wasIncomplete && willComplete) {
                                celebratingGoal = goal.copy(currentAmount = goal.currentAmount + amount)
                            }
                        },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }

    celebratingGoal?.let { goal ->
        GoalCelebrationOverlay(goal = goal, currencyFormat = currencyFormat, onDismiss = { celebratingGoal = null })
    }
}

/**
 * Celebracion de "meta lograda": el momento pico de esta pantalla (peak-end rule del
 * skill de diseño). Reutiliza la ilustracion savings_goal_illustration.jpg (antes sin
 * usar en ningun composable) + un par de destellos animados y un rebote de entrada.
 */
@Composable
private fun GoalCelebrationOverlay(
    goal: SavingsGoal,
    currencyFormat: NumberFormat,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow),
        label = "celebrationScale"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkleRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center
    ) {
        FinanceCard(
            modifier = Modifier
                .padding(32.dp)
                .scale(scale),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.savings_goal_illustration),
                        contentDescription = null,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = WarningAmber,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.TopEnd)
                            .rotate(sparkleRotation)
                    )
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = IncomeGreen,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomStart)
                            .rotate(-sparkleRotation)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "¡Meta lograda! 🎉",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Completaste \"${goal.name}\" con ${currencyFormat.money(goal.currentAmount)}. Tu constancia rindió frutos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen)
                ) {
                    Text("Genial")
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
    onAddProgress: (Long, Long) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var showAddProgressDialog by remember(goal.id) { mutableStateOf(false) }
    val progress = if (goal.targetAmount > 0) {
        (goal.currentAmount.toFloat() / goal.targetAmount.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val animatedProgress = rememberAnimatedFloat(progress)
    val isCompleted = goal.currentAmount >= goal.targetAmount

    FinanceCard(
        modifier = modifier.pressFeedback(onClick = onClick),
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
            progress = animatedProgress,
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
            AnimatedAmountText(
                target = goal.currentAmount,
                format = { currencyFormat.money(it) },
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isCompleted) {
                    "Objetivo alcanzado"
                } else {
                    "Faltan " + currencyFormat.money((goal.targetAmount - goal.currentAmount).coerceAtLeast(0))
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!isCompleted) {
                TextButton(onClick = { showAddProgressDialog = true }) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Abonar")
                }
            }
        }
    }

    if (showAddProgressDialog) {
        AddProgressDialog(
            goal = goal,
            onDismiss = { showAddProgressDialog = false },
            onConfirm = { amountCents ->
                goal.id?.let { onAddProgress(it, amountCents) }
                showAddProgressDialog = false
            }
        )
    }
}

/**
 * Abonar dinero a una meta de ahorro. Antes no existía ninguna forma de invocar
 * SavingsGoalsViewModel.addProgress() desde la UI -- este diálogo es el botón
 * "Abonar" que faltaba.
 */
@Composable
private fun AddProgressDialog(
    goal: SavingsGoal,
    onDismiss: () -> Unit,
    onConfirm: (amountCents: Long) -> Unit
) {
    var amountInput by rememberSaveable(goal.id) { mutableStateOf("") }
    val amountPesos = amountInput.toLongOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Abonar a \"${goal.name}\"", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = amountInput,
                onValueChange = { value -> if (value.all { it.isDigit() }) amountInput = value },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Monto a abonar (COP)") },
                prefix = { Text("$ ") }
            )
        },
        confirmButton = {
            Button(
                onClick = { amountPesos?.let { onConfirm(it * 100) } },
                enabled = amountPesos != null && amountPesos > 0
            ) {
                Text("Abonar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Crear/editar una meta de ahorro. Antes onAddGoal/onGoalClick eran no-ops en
 * SavingsGoalsScreen y no existía ninguna pantalla para crearlas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSavingsGoalScreen(
    goal: SavingsGoal?,
    onSave: (SavingsGoal) -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    var name by rememberSaveable { mutableStateOf(goal?.name ?: "") }
    var targetAmountInput by rememberSaveable {
        mutableStateOf(if (goal != null) (goal.targetAmount / 100L).toString() else "")
    }
    var targetDateInput by rememberSaveable {
        mutableStateOf(
            goal?.let { dateFormatter.format(it.targetDate.atZone(ZoneId.systemDefault())) }
                ?: dateFormatter.format(LocalDate.now().plusYears(1))
        )
    }

    val targetAmountCents = targetAmountInput.toLongOrNull()?.let { it * 100 }
    val targetDate = remember(targetDateInput) {
        try {
            LocalDate.parse(targetDateInput, dateFormatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text(if (goal == null) "Nueva meta" else "Editar meta") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                label = { Text("Nombre de la meta") }
            )
            OutlinedTextField(
                value = targetAmountInput,
                onValueChange = { value -> if (value.all { it.isDigit() }) targetAmountInput = value },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Monto objetivo (COP)") },
                prefix = { Text("$ ") }
            )
            OutlinedTextField(
                value = targetDateInput,
                onValueChange = { targetDateInput = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                isError = targetDate == null,
                label = { Text("Fecha límite (dd/MM/aaaa)") },
                supportingText = if (targetDate == null) {
                    { Text("Formato inválido, usa dd/MM/aaaa") }
                } else null
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
                        val amount = targetAmountCents ?: return@Button
                        val date = targetDate ?: return@Button
                        onSave(
                            SavingsGoal(
                                id = goal?.id,
                                name = name.trim(),
                                targetAmount = amount,
                                currentAmount = goal?.currentAmount ?: 0,
                                targetDate = date.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                                createdAt = goal?.createdAt ?: Instant.now()
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank() && targetAmountCents != null && targetAmountCents > 0 && targetDate != null,
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
                    Text("Eliminar meta")
                }
            }
        }
    }
}

private fun NumberFormat.money(amount: Long): String = format(amount / 100.0)

private fun SavingsGoal.targetDateLabel(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "CO"))
        .withZone(ZoneId.systemDefault())
    return formatter.format(targetDate)
}
