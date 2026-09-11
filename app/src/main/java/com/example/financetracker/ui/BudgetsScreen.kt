package com.example.financetracker.ui
import com.example.financetracker.theme.bounceClick

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.data.Budget
import com.example.financetracker.data.Category
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(viewModel: FinanceViewModel, modifier: Modifier = Modifier) {
    val categories by viewModel.categories.collectAsState()
    val allBudgets by viewModel.budgets.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    
    var showBudgetDialog by remember { mutableStateOf<Category?>(null) }
    var budgetAmountInput by remember { mutableStateOf("") }
    
    val cal = Calendar.getInstance()
    val currentMonth = cal.get(Calendar.MONTH)
    val currentYear = cal.get(Calendar.YEAR)
    
    val salaryCat = categories.find { it.name.equals("Salary", ignoreCase = true) }
    
    // Salary calculation
    val monthStart = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    
    val prevMonthStart = Calendar.getInstance().apply {
        add(Calendar.MONTH, -1)
        set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    
    var currentSalary = transactions.filter { it.categoryId == salaryCat?.id && it.timestamp >= monthStart && it.type == "Credit" }.sumOf { it.amount }
    if (currentSalary == 0.0) {
        currentSalary = transactions.filter { it.categoryId == salaryCat?.id && it.timestamp in prevMonthStart until monthStart && it.type == "Credit" }.sumOf { it.amount }
    }
    
    // Resolve current month budgets with carryover logic
    val currentMonthBudgets = categories.associate { cat ->
        val explicit = allBudgets.find { it.categoryId == cat.id && it.month == currentMonth && it.year == currentYear }
        if (explicit != null) {
            cat to explicit.amount
        } else {
            // Find most recent previous budget
            val previous = allBudgets.filter { it.categoryId == cat.id && (it.year < currentYear || (it.year == currentYear && it.month < currentMonth)) }
                .maxByOrNull { it.year * 12 + it.month }
            cat to (previous?.amount ?: 0.0)
        }
    }
    
    val totalBudget = currentMonthBudgets.values.sum()
    val remainingSalary = currentSalary - totalBudget
    
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val dailyAvgBudget = totalBudget / maxDays
    
    Column(modifier = modifier.fillMaxSize()) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 12.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Salary & Budgets", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "₹${"%.2f".format(remainingSalary)} Remaining", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = if (remainingSalary >= 0) MaterialTheme.colorScheme.primary else Color.Red, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Salary: ₹${"%.2f".format(currentSalary)} | Budgeted: ₹${"%.2f".format(totalBudget)} / mo", fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Daily budget allowance: ₹${"%.2f".format(dailyAvgBudget)} / day", fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        
        Text("Manage Monthly Budgets", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        
        var budgetToDelete by remember { mutableStateOf<Category?>(null) }
        
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()
        val view = androidx.compose.ui.platform.LocalView.current
        val isHapticsEnabled = com.example.financetracker.theme.LocalHapticEnabled.current
        LaunchedEffect(listState.firstVisibleItemIndex) { if(isHapticsEnabled) view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK) }
        LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
            items(categories.filter { !it.name.equals("Salary", ignoreCase = true) }) { cat ->
                val bAmt = currentMonthBudgets[cat] ?: 0.0
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).bounceClick(
                    onClick = {
                        showBudgetDialog = cat 
                        budgetAmountInput = if (bAmt > 0) bAmt.toString() else ""
                    },
                    onLongClick = {
                        if (bAmt > 0) budgetToDelete = cat
                    }
                ), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), shape = RoundedCornerShape(16.dp)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(cat.name, fontWeight = FontWeight.Bold)
                        Text(if (bAmt > 0) "₹${"%.2f".format(bAmt)}" else "Not Set", color = if (bAmt > 0) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                }
            }
        }

        if (budgetToDelete != null) {
            AlertDialog(
                onDismissRequest = { budgetToDelete = null },
                title = { Text("Delete Budget") },
                text = { Text("Are you sure you want to remove the budget for ${budgetToDelete?.name}?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.saveBudget(Budget(categoryId = budgetToDelete!!.id, amount = 0.0, month = currentMonth, year = currentYear))
                        budgetToDelete = null
                    }) { Text("Yes") }
                },
                dismissButton = {
                    TextButton(onClick = { budgetToDelete = null }) { Text("No") }
                }
            )
        }
    }
    
    if (showBudgetDialog != null) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = null },
            title = { Text("Set Budget for ${showBudgetDialog?.name}") },
            text = {
                OutlinedTextField(
                    value = budgetAmountInput,
                    onValueChange = { budgetAmountInput = it },
                    label = { Text("Amount") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val amount = budgetAmountInput.toDoubleOrNull() ?: 0.0
                    viewModel.saveBudget(Budget(categoryId = showBudgetDialog!!.id, amount = amount, month = currentMonth, year = currentYear))
                    showBudgetDialog = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = null }) { Text("Cancel") }
            }
        )
    }
}
