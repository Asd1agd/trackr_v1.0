package com.example.financetracker.ui
import com.example.financetracker.theme.bounceClick

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.activity.compose.BackHandler
import com.example.financetracker.data.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: FinanceViewModel, startWithAddDialog: Boolean = false) {
    var selectedTab by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(0) }
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showAddDialog by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(startWithAddDialog) }
    var showProfileSettings by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    val tabs = listOf(
        Triple(Icons.Default.PieChart, "Home", 0),
        Triple(Icons.AutoMirrored.Filled.List, "History", 1),
        Triple(Icons.Default.Star, "Goals", 2),
        Triple(Icons.Default.AccountBalanceWallet, "Budget", 3),
        Triple(Icons.Default.Insights, "Analysis", 4)
    )

    AnimatedContent(
        targetState = showProfileSettings,
        transitionSpec = {
            slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { fullWidth -> fullWidth }
            ) togetherWith slideOutHorizontally(
                animationSpec = tween(300),
                targetOffsetX = { fullWidth -> -fullWidth }
            )
        },
        label = "ProfileSettingsAnimation"
    ) { isShowingProfile ->
        if (isShowingProfile) {
            BackHandler { showProfileSettings = false }
            ProfileSettingsScreen(viewModel, 
                onBack = { showProfileSettings = false },
                onSave = { showProfileSettings = false; viewModel.checkAndInjectMonthlySalary() }
            )
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        tonalElevation = 0.dp,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        tabs.forEach { (icon, label, index) ->
                            NavigationBarItem(
                                icon = { Icon(icon, contentDescription = label) },
                                label = {
                                    Text(
                                        label,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                        softWrap = false
                                    )
                                },
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = androidx.compose.foundation.shape.CircleShape
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                    }
                },
                floatingActionButtonPosition = FabPosition.Center
            ) { padding ->
                val modifier = Modifier.padding(padding)
                when (selectedTab) {
                    0 -> DashboardScreen(viewModel, modifier, onNavigateToTransactions = { selectedTab = 1 }, onOpenProfile = { showProfileSettings = true })
                    1 -> TransactionsScreen(viewModel, modifier)
                    2 -> GoalsScreen(viewModel, modifier)
                    3 -> BudgetsScreen(viewModel, modifier)
                    4 -> AnalysisScreen(viewModel, modifier)
                }
            }
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
