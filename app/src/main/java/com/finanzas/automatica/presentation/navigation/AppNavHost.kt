package com.finanzas.automatica.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.presentation.ui.screen.AgendaScreen
import com.finanzas.automatica.presentation.ui.screen.BudgetsScreen
import com.finanzas.automatica.presentation.ui.screen.DashboardScreen
import com.finanzas.automatica.presentation.ui.screen.MovementsListScreen
import com.finanzas.automatica.presentation.ui.screen.SavingsGoalsScreen
import com.finanzas.automatica.presentation.ui.screen.SettingsScreen
import com.finanzas.automatica.presentation.viewmodel.AgendaViewModel
import com.finanzas.automatica.presentation.viewmodel.BudgetsViewModel
import com.finanzas.automatica.presentation.viewmodel.MovementViewModel
import com.finanzas.automatica.presentation.viewmodel.SavingsGoalsViewModel
import com.finanzas.automatica.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(database: FinanzasDatabase) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()

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
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
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
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                val movementViewModel: MovementViewModel = databaseViewModel {
                    MovementViewModel(database)
                }
                val movements by movementViewModel.movements.collectAsState()
                val pendingCount by movementViewModel.pendingCount.collectAsState()

                DashboardScreen(
                    movements = movements,
                    pendingCount = pendingCount,
                    onPendingClick = { navController.navigate(Screen.Movements.pendingRoute) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
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
                    MovementViewModel(database)
                }
                val movements by movementViewModel.movements.collectAsState()
                val initialFilter = backStackEntry.arguments?.getString("filter") ?: "all"

                MovementsListScreen(
                    movements = movements,
                    initialFilter = initialFilter,
                    onMovementClick = {},
                    onConfirm = movementViewModel::confirmMovement,
                    onReject = movementViewModel::rejectMovement
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
                    onEntryClick = {},
                    onAddEntry = {}
                )
            }

            composable(Screen.Budgets.route) {
                val budgetsViewModel: BudgetsViewModel = databaseViewModel {
                    BudgetsViewModel(database)
                }
                val budgets by budgetsViewModel.budgets.collectAsState()
                val categories by budgetsViewModel.categories.collectAsState()

                BudgetsScreen(
                    budgets = budgets,
                    categories = categories,
                    onBudgetClick = {},
                    onAddBudget = {}
                )
            }

            composable(Screen.Savings.route) {
                val savingsGoalsViewModel: SavingsGoalsViewModel = databaseViewModel {
                    SavingsGoalsViewModel(database)
                }
                val goals by savingsGoalsViewModel.goals.collectAsState()

                SavingsGoalsScreen(
                    goals = goals,
                    onGoalClick = {},
                    onAddGoal = {},
                    onAddProgress = savingsGoalsViewModel::addProgress
                )
            }

            composable(Screen.Settings.route) {
                val context = LocalContext.current
                val settingsViewModel: SettingsViewModel = databaseViewModel {
                    SettingsViewModel(database, context)
                }
                val notificationsEnabled by settingsViewModel.notificationsEnabled.collectAsState()
                val autoConfirmHighConfidence by settingsViewModel.autoConfirmHighConfidence.collectAsState()
                val biometricEnabled by settingsViewModel.biometricEnabled.collectAsState()
                val exportDataFormat by settingsViewModel.exportDataFormat.collectAsState()

                SettingsScreen(
                    notificationsEnabled = notificationsEnabled,
                    autoConfirmHighConfidence = autoConfirmHighConfidence,
                    biometricEnabled = biometricEnabled,
                    exportDataFormat = exportDataFormat,
                    onNotificationsChange = settingsViewModel::setNotificationsEnabled,
                    onAutoConfirmChange = settingsViewModel::setAutoConfirmHighConfidence,
                    onBiometricChange = settingsViewModel::setBiometricEnabled,
                    onExportFormatChange = settingsViewModel::setExportFormat,
                    onExportData = { settingsViewModel.exportData() },
                    onDeleteData = { settingsViewModel.deleteAllData() }
                )
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
    object Agenda : Screen("agenda", "agenda", "Agenda", Icons.Outlined.Contacts)
    object Budgets : Screen("budgets", "budgets", "Planes", Icons.Outlined.AccountBalance)
    object Savings : Screen("savings", "savings", "Metas", Icons.Outlined.Savings)
    object Settings : Screen("settings", "settings", "Ajustes", Icons.Outlined.Settings)

    companion object {
        val bottomItems = listOf(Dashboard, Movements, Agenda, Budgets, Savings, Settings)
    }
}
