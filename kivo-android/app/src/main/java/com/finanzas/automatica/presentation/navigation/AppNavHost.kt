package com.finanzas.automatica.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.finanzas.automatica.presentation.ui.components.FinanceCard
import com.finanzas.automatica.presentation.ui.components.FinanceTag
import com.finanzas.automatica.presentation.ui.components.IconBadge
import com.finanzas.automatica.presentation.ui.components.rememberNotificationAccessEnabled
import com.finanzas.automatica.presentation.ui.theme.InfoBlue
import com.finanzas.automatica.presentation.ui.theme.IncomeGreen
import com.finanzas.automatica.presentation.ui.theme.WarningAmber
import com.finanzas.automatica.presentation.ui.theme.AppThemePalette
import com.finanzas.automatica.presentation.ui.theme.ThemeMode
import com.finanzas.automatica.data.local.FinanzasDatabase
import androidx.compose.material.icons.outlined.ReceiptLong
import com.finanzas.automatica.presentation.ui.screen.AddEditAgendaEntryScreen
import com.finanzas.automatica.presentation.ui.screen.AddEditBudgetScreen
import com.finanzas.automatica.presentation.ui.screen.AddEditSavingsGoalScreen
import com.finanzas.automatica.presentation.ui.screen.AgendaScreen
import com.finanzas.automatica.presentation.ui.screen.LoginScreen
import com.finanzas.automatica.presentation.ui.screen.BudgetsScreen
import com.finanzas.automatica.presentation.ui.screen.DashboardScreen
import com.finanzas.automatica.presentation.ui.screen.InvoiceScreen
import com.finanzas.automatica.presentation.ui.screen.MovementsListScreen
import com.finanzas.automatica.presentation.ui.screen.NotificationCenterScreen
import com.finanzas.automatica.presentation.ui.screen.SavingsGoalsScreen
import com.finanzas.automatica.presentation.ui.screen.SettingsScreen
import com.finanzas.automatica.presentation.viewmodel.AgendaViewModel
import com.finanzas.automatica.presentation.viewmodel.BudgetsViewModel
import com.finanzas.automatica.presentation.viewmodel.InvoiceViewModel
import com.finanzas.automatica.presentation.viewmodel.MovementViewModel
import com.finanzas.automatica.presentation.viewmodel.NotificationCenterViewModel
import com.finanzas.automatica.presentation.viewmodel.SessionViewModel
import com.finanzas.automatica.presentation.viewmodel.SavingsGoalsViewModel
import com.finanzas.automatica.presentation.viewmodel.SettingsViewModel
import com.finanzas.automatica.service.BiometricAccess
import com.finanzas.automatica.service.NotificationAccess
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(database: FinanzasDatabase) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionViewModel: SessionViewModel = databaseViewModel {
        SessionViewModel(context)
    }
    val sessionState by sessionViewModel.sessionState.collectAsState()
    val notificationCenterViewModel: NotificationCenterViewModel = databaseViewModel {
        NotificationCenterViewModel(database)
    }
    val notificationUnread by notificationCenterViewModel.unreadCount.collectAsState()

    fun openDrawer() {
        scope.launch {
            drawerState.open()
        }
    }

    fun navigateTo(route: String) {
        scope.launch {
            drawerState.close()
        }
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Kivo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Tu dinero, en orden",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FinanceCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconBadge(
                                icon = Icons.Outlined.Person,
                                contentDescription = "Cuenta",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = if (sessionState.isConnected) "Sesion web activa" else "Sin sesion web",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (sessionState.email.isBlank()) {
                                        "Conecta el panel web para preparar la sincronizacion."
                                    } else {
                                        sessionState.email
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FinanceTag(
                                text = if (sessionState.isConnected) "Conectado" else "Desconectado",
                                color = if (sessionState.isConnected) IncomeGreen else WarningAmber,
                                containerColor = if (sessionState.isConnected) IncomeGreen.copy(alpha = 0.12f) else WarningAmber.copy(alpha = 0.12f)
                            )
                            FinanceTag(
                                text = if (sessionState.lastSyncAt != null) "Sync listo" else "Sin sync",
                                color = InfoBlue,
                                containerColor = InfoBlue.copy(alpha = 0.12f)
                            )
                        }
                    }

                    NavigationDrawerItem(
                        label = { Text("Inicio") },
                        selected = currentRoute.startsWith(Screen.Dashboard.selectedPrefix),
                        onClick = { navigateTo(Screen.Dashboard.route) },
                        icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors()
                    )
                    NavigationDrawerItem(
                        label = { Text("Movimientos") },
                        selected = currentRoute.startsWith(Screen.Movements.selectedPrefix),
                        onClick = { navigateTo(Screen.Movements.route) },
                        icon = { Icon(Icons.Outlined.FormatListBulleted, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors()
                    )
                    NavigationDrawerItem(
                        label = { Text("Facturas y Deudas") },
                        selected = currentRoute.startsWith(Screen.Invoices.selectedPrefix),
                        onClick = { navigateTo(Screen.Invoices.route) },
                        icon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors()
                    )
                    NavigationDrawerItem(
                        label = { Text("Agenda") },
                        selected = currentRoute.startsWith(Screen.Agenda.selectedPrefix),
                        onClick = { navigateTo(Screen.Agenda.route) },
                        icon = { Icon(Icons.Outlined.Contacts, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors()
                    )
                    NavigationDrawerItem(
                        label = { Text("Planes") },
                        selected = currentRoute.startsWith(Screen.Budgets.selectedPrefix),
                        onClick = { navigateTo(Screen.Budgets.route) },
                        icon = { Icon(Icons.Outlined.AccountBalance, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors()
                    )
                    NavigationDrawerItem(
                        label = { Text("Metas") },
                        selected = currentRoute.startsWith(Screen.Savings.selectedPrefix),
                        onClick = { navigateTo(Screen.Savings.route) },
                        icon = { Icon(Icons.Outlined.Savings, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors()
                    )
                    NavigationDrawerItem(
                        label = { Text("Notificaciones") },
                        selected = currentRoute.startsWith(Screen.Notifications.selectedPrefix),
                        onClick = { navigateTo(Screen.Notifications.route) },
                        icon = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
                        badge = {
                            if (notificationUnread > 0) {
                                FinanceTag(
                                    text = notificationUnread.toString(),
                                    color = MaterialTheme.colorScheme.primary,
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors()
                    )

                    Divider()

                    NavigationDrawerItem(
                        label = { Text("Cuenta y sincronizacion") },
                        selected = currentRoute.startsWith(Screen.Login.selectedPrefix),
                        onClick = { navigateTo(Screen.Login.route) },
                        icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors()
                    )
                    NavigationDrawerItem(
                        label = { Text("Ajustes") },
                        selected = currentRoute.startsWith(Screen.Settings.selectedPrefix),
                        onClick = { navigateTo(Screen.Settings.route) },
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors()
                    )
                }
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    Screen.bottomItems.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute.startsWith(screen.selectedPrefix),
                            onClick = { navigateTo(screen.route) },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    fadeIn(animationSpec = tween(240)) +
                        slideInVertically(animationSpec = tween(240)) { it / 24 }
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(160))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(240))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(160)) +
                        slideOutVertically(animationSpec = tween(160)) { it / 24 }
                }
            ) {
            composable(Screen.Dashboard.route) {
                val movementViewModel: MovementViewModel = databaseViewModel {
                    MovementViewModel(database, context)
                }
                val movements by movementViewModel.movements.collectAsState()
                val pendingCount by movementViewModel.pendingCount.collectAsState()
                val notificationAccessEnabled = rememberNotificationAccessEnabled()

                DashboardScreen(
                    movements = movements,
                    pendingCount = pendingCount,
                    notificationAccessEnabled = notificationAccessEnabled,
                    onEnableNotificationAccess = { NotificationAccess.openSettings(context) },
                    onPendingClick = { navigateTo(Screen.Movements.pendingRoute) },
                    onSettingsClick = { navigateTo(Screen.Settings.route) },
                    onOpenMenu = ::openDrawer
                )
            }

            composable(
                route = Screen.Movements.routePattern,
                arguments = listOf(
                    navArgument("filter") {
                        type = NavType.StringType
                        defaultValue = "all"
                    }
                )
            ) { backStackEntry ->
                val movementViewModel: MovementViewModel = databaseViewModel {
                    MovementViewModel(database, context)
                }
                val movements by movementViewModel.movements.collectAsState()
                val movementCategories by movementViewModel.categories.collectAsState()
                val initialFilter = backStackEntry.arguments?.getString("filter") ?: "all"

                MovementsListScreen(
                    movements = movements,
                    initialFilter = initialFilter,
                    categories = movementCategories,
                    onConfirm = movementViewModel::confirmMovement,
                    onReject = movementViewModel::rejectMovement,
                    onCorrect = movementViewModel::correctMovement,
                    onImportStatement = movementViewModel::importStatementText,
                    onImportPdf = movementViewModel::importStatementPdf,
                    onImportScreenshot = movementViewModel::importScreenshot,
                    onOpenMenu = ::openDrawer
                )
            }

            composable(Screen.Invoices.route) {
                val invoiceViewModel: InvoiceViewModel = databaseViewModel {
                    InvoiceViewModel(database, context)
                }
                val invoices by invoiceViewModel.invoices.collectAsState()
                val debtSummaries by invoiceViewModel.debtSummaries.collectAsState()
                val allDebts by invoiceViewModel.allDebts.collectAsState()
                val contacts by invoiceViewModel.contacts.collectAsState()

                InvoiceScreen(
                    invoices = invoices,
                    debtSummaries = debtSummaries,
                    allDebts = allDebts,
                    contacts = contacts,
                    onSaveInvoice = invoiceViewModel::saveInvoice,
                    onMarkDebtPaid = invoiceViewModel::markDebtAsPaid,
                    onDeleteInvoice = invoiceViewModel::deleteInvoice,
                    onScanReceiptBitmap = invoiceViewModel::scanReceiptBitmap,
                    onScanReceiptUri = invoiceViewModel::scanReceiptUri,
                    onLoadSample = invoiceViewModel::createSampleParsedInvoice,
                    onOpenMenu = ::openDrawer
                )
            }

            composable(Screen.Agenda.route) {
                val agendaViewModel: AgendaViewModel = databaseViewModel {
                    AgendaViewModel(database)
                }
                val entries by agendaViewModel.entries.collectAsState()
                val categories by agendaViewModel.categories.collectAsState()

                AgendaScreen(
                    agendaEntries = entries,
                    categories = categories,
                    onEntryClick = { entry -> navigateTo(Screen.agendaEditRoute(entry.id)) },
                    onAddEntry = { navigateTo(Screen.agendaEditRoute()) },
                    onOpenMenu = ::openDrawer
                )
            }

            composable(
                route = Screen.AGENDA_EDIT_PATTERN,
                arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
            ) { backStackEntry ->
                val agendaViewModel: AgendaViewModel = databaseViewModel {
                    AgendaViewModel(database)
                }
                val entries by agendaViewModel.entries.collectAsState()
                val categories by agendaViewModel.categories.collectAsState()
                val id = backStackEntry.arguments?.getLong("id") ?: -1L
                val entry = entries.find { it.id == id }

                AddEditAgendaEntryScreen(
                    entry = entry,
                    categories = categories,
                    onSave = { updated ->
                        if (entry == null) agendaViewModel.addEntry(updated) else agendaViewModel.updateEntry(updated)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() },
                    onDelete = entry?.let { existing ->
                        { agendaViewModel.deleteEntry(existing.id); navController.popBackStack() }
                    }
                )
            }

            composable(Screen.Budgets.route) {
                val budgetsViewModel: BudgetsViewModel = databaseViewModel {
                    BudgetsViewModel(database)
                }
                val budgets by budgetsViewModel.budgets.collectAsState()
                val categories by budgetsViewModel.categories.collectAsState()
                val spentByBudgetKey by budgetsViewModel.spentByBudgetKey.collectAsState()

                BudgetsScreen(
                    budgets = budgets,
                    categories = categories,
                    spentByBudgetKey = spentByBudgetKey,
                    onBudgetClick = { budget -> navigateTo(Screen.budgetEditRoute(budget.id)) },
                    onAddBudget = { navigateTo(Screen.budgetEditRoute()) },
                    onOpenMenu = ::openDrawer
                )
            }

            composable(
                route = Screen.BUDGET_EDIT_PATTERN,
                arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
            ) { backStackEntry ->
                val budgetsViewModel: BudgetsViewModel = databaseViewModel {
                    BudgetsViewModel(database)
                }
                val budgets by budgetsViewModel.budgets.collectAsState()
                val categories by budgetsViewModel.categories.collectAsState()
                val id = backStackEntry.arguments?.getLong("id") ?: -1L
                val budget = budgets.find { it.id == id }

                AddEditBudgetScreen(
                    budget = budget,
                    categories = categories,
                    onSave = { updated ->
                        if (budget == null) budgetsViewModel.addBudget(updated) else budgetsViewModel.updateBudget(updated)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() },
                    onDelete = budget?.id?.let { budgetId ->
                        { budgetsViewModel.deleteBudget(budgetId); navController.popBackStack() }
                    }
                )
            }

            composable(Screen.Savings.route) {
                val savingsGoalsViewModel: SavingsGoalsViewModel = databaseViewModel {
                    SavingsGoalsViewModel(database)
                }
                val goals by savingsGoalsViewModel.goals.collectAsState()

                SavingsGoalsScreen(
                    goals = goals,
                    onGoalClick = { goal -> navigateTo(Screen.savingsEditRoute(goal.id)) },
                    onAddGoal = { navigateTo(Screen.savingsEditRoute()) },
                    onAddProgress = savingsGoalsViewModel::addProgress,
                    onOpenMenu = ::openDrawer
                )
            }

            composable(
                route = Screen.SAVINGS_EDIT_PATTERN,
                arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
            ) { backStackEntry ->
                val savingsGoalsViewModel: SavingsGoalsViewModel = databaseViewModel {
                    SavingsGoalsViewModel(database)
                }
                val goals by savingsGoalsViewModel.goals.collectAsState()
                val id = backStackEntry.arguments?.getLong("id") ?: -1L
                val goal = goals.find { it.id == id }

                AddEditSavingsGoalScreen(
                    goal = goal,
                    onSave = { updated ->
                        if (goal == null) savingsGoalsViewModel.addGoal(updated) else savingsGoalsViewModel.updateGoal(updated)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() },
                    onDelete = goal?.id?.let { goalId ->
                        { savingsGoalsViewModel.deleteGoal(goalId); navController.popBackStack() }
                    }
                )
            }

            composable(Screen.Notifications.route) {
                val notifications by notificationCenterViewModel.notifications.collectAsState()
                NotificationCenterScreen(
                    notifications = notifications,
                    unreadCount = notificationUnread,
                    onMarkAllRead = notificationCenterViewModel::markAllRead,
                    onMarkRead = notificationCenterViewModel::markRead,
                    onOpenMenu = ::openDrawer
                )
            }

            composable(Screen.Settings.route) {
                val context = LocalContext.current
                val settingsViewModel: SettingsViewModel = databaseViewModel {
                    SettingsViewModel(database, context)
                }
                val autoConfirmHighConfidence by settingsViewModel.autoConfirmHighConfidence.collectAsState()
                val biometricEnabled by settingsViewModel.biometricEnabled.collectAsState()
                val exportDataFormat by settingsViewModel.exportDataFormat.collectAsState()
                val themeMode by settingsViewModel.themeMode.collectAsState()
                val themePalette by settingsViewModel.themePalette.collectAsState()

                SettingsScreen(
                    notificationAccessEnabled = rememberNotificationAccessEnabled(),
                    autoConfirmHighConfidence = autoConfirmHighConfidence,
                    biometricEnabled = biometricEnabled,
                    isBiometricAvailable = BiometricAccess.isAvailable(context),
                    exportDataFormat = exportDataFormat,
                    themeMode = themeMode,
                    themePalette = themePalette,
                    onEnableNotificationAccess = { NotificationAccess.openSettings(context) },
                    onAutoConfirmChange = settingsViewModel::setAutoConfirmHighConfidence,
                    onBiometricChange = { enabled ->
                        if (enabled && !BiometricAccess.isAvailable(context)) {
                            android.widget.Toast.makeText(
                                context,
                                "Configura primero huella, rostro o PIN en los ajustes del dispositivo",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else {
                            settingsViewModel.setBiometricEnabled(enabled)
                        }
                    },
                    onExportFormatChange = settingsViewModel::setExportFormat,
                    onExportData = { settingsViewModel.exportData() },
                    onDeleteData = { settingsViewModel.deleteAllData() },
                    onAccountClick = { navigateTo(Screen.Login.route) },
                    onThemeModeChange = settingsViewModel::setThemeMode,
                    onThemePaletteChange = settingsViewModel::setThemePalette,
                    onOpenMenu = ::openDrawer
                )
            }

            composable(Screen.Login.route) {
                val context = LocalContext.current
                val sessionVm: SessionViewModel = databaseViewModel { SessionViewModel(context) }
                val settingsViewModel: SettingsViewModel = databaseViewModel {
                    SettingsViewModel(database, context)
                }
                val sessionState by sessionVm.sessionState.collectAsState()

                LoginScreen(
                    isConnected = sessionState.isConnected,
                    email = sessionState.email,
                    backendUrl = sessionState.backendUrl,
                    lastSyncLabel = sessionVm.lastSyncLabel(),
                    onConnect = sessionVm::connect,
                    onDisconnect = sessionVm::disconnect,
                    onSyncNow = {
                        settingsViewModel.prepareSyncSnapshot()
                        sessionVm.markSynced()
                    },
                    onOpenMenu = ::openDrawer
                )
            }
        }
    }
}

}

@Composable
private inline fun <reified VM : ViewModel> databaseViewModel(
    crossinline creator: () -> VM
): VM {
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return creator() as T
            }
        }
    )
}

sealed class Screen(
    val route: String,
    val selectedPrefix: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : Screen("dashboard", "dashboard", "Inicio", Icons.Outlined.Home)
    object Movements : Screen("movements?filter=all", "movements", "Movimientos", Icons.Outlined.FormatListBulleted) {
        const val routePattern = "movements?filter={filter}"
        const val pendingRoute = "movements?filter=pending"
    }
    object Invoices : Screen("invoices", "invoices", "Facturas", Icons.Outlined.ReceiptLong)
    object Agenda : Screen("agenda", "agenda", "Agenda", Icons.Outlined.Contacts)
    object Budgets : Screen("budgets", "budgets", "Planes", Icons.Outlined.AccountBalance)
    object Savings : Screen("savings", "savings", "Metas", Icons.Outlined.Savings)
    object Notifications : Screen("notifications", "notifications", "Notificaciones", Icons.Outlined.Notifications)
    object Login : Screen("login", "login", "Cuenta", Icons.Outlined.Person)
    object Settings : Screen("settings", "settings", "Ajustes", Icons.Outlined.Settings)

    companion object {
        val bottomItems: List<Screen>
            get() = listOf(Dashboard, Movements, Invoices, Agenda, Budgets)

        // Rutas de crear/editar (no son destinos del menu, por eso no son objetos Screen
        // completos): -1 significa "nuevo registro", cualquier otro valor es el id a editar.
        const val AGENDA_EDIT_PATTERN = "agenda/edit?id={id}"
        const val BUDGET_EDIT_PATTERN = "budgets/edit?id={id}"
        const val SAVINGS_EDIT_PATTERN = "savings/edit?id={id}"

        fun agendaEditRoute(id: Long = -1L) = "agenda/edit?id=$id"
        fun budgetEditRoute(id: Long? = null) = "budgets/edit?id=${id ?: -1L}"
        fun savingsEditRoute(id: Long? = null) = "savings/edit?id=${id ?: -1L}"
    }
}
