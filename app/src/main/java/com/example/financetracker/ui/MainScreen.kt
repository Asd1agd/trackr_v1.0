package com.example.financetracker.ui
import com.example.financetracker.theme.bounceClick

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.financetracker.data.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: FinanceViewModel, startWithAddDialog: Boolean = false) {
    var selectedTab by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(0) }
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showAddDialog by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(startWithAddDialog) }
    var showProfileSettings by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    if (showProfileSettings) {
        ProfileSettingsScreen(onBack = { showProfileSettings = false }, onSave = { showProfileSettings = false; viewModel.checkAndInjectMonthlySalary() })
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.PieChart, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, softWrap = false) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Transactions") },
                    label = { Text("Transactions", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, softWrap = false) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Goals") },
                    label = { Text("Goals", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, softWrap = false) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Budgets") },
                    label = { Text("Budgets", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, softWrap = false) },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Analysis") },
                    label = { Text("Analysis", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, softWrap = false) },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        val modifier = Modifier.padding(padding)
        if (selectedTab == 0) {
            DashboardScreen(viewModel, modifier, onNavigateToTransactions = { selectedTab = 1 }, onOpenProfile = { showProfileSettings = true })
        } else if (selectedTab == 1) {
            TransactionsScreen(viewModel, modifier)
        } else if (selectedTab == 2) {
            GoalsScreen(viewModel, modifier)
        } else if (selectedTab == 3) {
            BudgetsScreen(viewModel, modifier)
        } else {
            AnalysisScreen(viewModel, modifier)
        }
    }

    var showYamlDialog by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }

    if (showYamlDialog) {
        YamlDialog(viewModel = viewModel, onDismiss = { showYamlDialog = false })
    }

    if (showAddDialog) {
        TransactionDialog(
            transaction = null,
            categories = categories,
            onDismiss = { showAddDialog = false },
            onSwitchToYaml = {
                showAddDialog = false
                showYamlDialog = true
            },
            onSave = { amount, type, note, catId, timestamp ->
                viewModel.addManualTransactionWithCategory(amount, type, note, timestamp, catId)
                showAddDialog = false
            },
            onAddCategory = { newCategory ->
                viewModel.addCategory(newCategory)
            }
        )
    }
}
