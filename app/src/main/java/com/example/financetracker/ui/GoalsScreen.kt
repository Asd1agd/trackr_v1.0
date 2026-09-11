package com.example.financetracker.ui
import com.example.financetracker.theme.bounceClick

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.data.Goal
import com.example.financetracker.data.Budget
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(viewModel: FinanceViewModel, modifier: Modifier = Modifier) {
    val goals by viewModel.goals.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var showEditGoalDialog by remember { mutableStateOf<Goal?>(null) }
    var editGoalName by remember { mutableStateOf("") }
    var editGoalTarget by remember { mutableStateOf("") }
    var editGoalCurrent by remember { mutableStateOf("") }
    
    var goalToDelete by remember { mutableStateOf<Goal?>(null) }

    var compensateGoal by remember { mutableStateOf<Goal?>(null) }
    var compensateBudget by remember { mutableStateOf<Budget?>(null) }
    var compensateAmountInput by remember { mutableStateOf("") }

    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val allBudgets by viewModel.budgets.collectAsState()
    val essentialCategoryIds by viewModel.essentialCategoryIds.collectAsState()
    
    val cal = Calendar.getInstance()
    val currentMonth = cal.get(Calendar.MONTH)
    val currentYear = cal.get(Calendar.YEAR)
    val monthStart = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
    val prevMonthStart = Calendar.getInstance().apply { add(Calendar.MONTH, -1); set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
    
    val salaryCat = categories.find { it.name.equals("Salary", ignoreCase = true) }
    var currentSalary = transactions.filter { it.categoryId == salaryCat?.id && it.timestamp >= monthStart && it.type == "Credit" }.sumOf { it.amount }
    if (currentSalary == 0.0) {
        currentSalary = transactions.filter { it.categoryId == salaryCat?.id && it.timestamp in prevMonthStart until monthStart && it.type == "Credit" }.sumOf { it.amount }
    }
    
    // Resolve current month budgets
    val currentMonthBudgets = categories.associate { cat ->
        val explicit = allBudgets.find { it.categoryId == cat.id && it.month == currentMonth && it.year == currentYear }
        if (explicit != null) {
            cat to explicit
        } else {
            val previous = allBudgets.filter { it.categoryId == cat.id && (it.year < currentYear || (it.year == currentYear && it.month < currentMonth)) }
                .maxByOrNull { it.year * 12 + it.month }
            if (previous != null) {
                cat to previous.copy(id = 0, month = currentMonth, year = currentYear)
            } else {
                cat to Budget(categoryId = cat.id, amount = 0.0, month = currentMonth, year = currentYear)
            }
        }
    }
    
    val totalBudget = currentMonthBudgets.values.sumOf { it.amount }
    
    val currentTimestamp = System.currentTimeMillis()
    val totalGoalsMonthlyRequirement = goals.sumOf { goal ->
        val remainingAmount = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)
        val remainingMonths = ((goal.targetDate - currentTimestamp) / (1000L * 60 * 60 * 24 * 30)).coerceAtLeast(1).toInt()
        remainingAmount / remainingMonths
    }
    
    val overallRemaining = currentSalary - totalBudget - totalGoalsMonthlyRequirement

    Column(modifier = modifier.fillMaxSize()) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 12.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Salary, Budgets & Goals", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "₹${"%.2f".format(overallRemaining)} Remaining", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = if (overallRemaining >= 0) MaterialTheme.colorScheme.primary else Color.Red, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Salary: ₹${"%.2f".format(currentSalary)}", fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Budgets: ₹${"%.2f".format(totalBudget)} | Goals: ₹${"%.2f".format(totalGoalsMonthlyRequirement)}/mo", fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Savings Goals", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 16.dp))
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
        
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()
        val view = androidx.compose.ui.platform.LocalView.current
        val isHapticsEnabled = com.example.financetracker.theme.LocalHapticEnabled.current
        LaunchedEffect(listState.firstVisibleItemIndex) { if(isHapticsEnabled) view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK) }
        LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
            items(goals) { goal ->
                GoalItem(
                    goal = goal,
                    overallRemaining = overallRemaining,
                    onEdit = { 
                        showEditGoalDialog = goal
                        editGoalName = goal.name
                        editGoalTarget = goal.targetAmount.toString()
                        editGoalCurrent = goal.currentAmount.toString()
                    },
                    onDelete = { goalToDelete = goal },
                    onCompensate = { compensateGoal = goal }
                )
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var targetAmount by remember { mutableStateOf("") }
        var showDatePicker by remember { mutableStateOf(false) }
        var targetDate by remember { mutableStateOf(System.currentTimeMillis() + 86400000L * 30) }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = targetDate)

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("New Goal") },
            text = {
                Column {
                    TextField(value = name, onValueChange = { name = it }, label = { Text("Goal Name") })
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(value = targetAmount, onValueChange = { targetAmount = it }, label = { Text("Target Amount") })
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(targetDate))
                    OutlinedButton(onClick = { showDatePicker = true }) {
                        Text("Target Date: $dateStr")
                    }
                    
                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    val utcCal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply { 
                                        timeInMillis = datePickerState.selectedDateMillis ?: targetDate 
                                    }
                                    val localCal = java.util.Calendar.getInstance().apply { timeInMillis = targetDate }
                                    localCal.set(java.util.Calendar.YEAR, utcCal.get(java.util.Calendar.YEAR))
                                    localCal.set(java.util.Calendar.MONTH, utcCal.get(java.util.Calendar.MONTH))
                                    localCal.set(java.util.Calendar.DAY_OF_MONTH, utcCal.get(java.util.Calendar.DAY_OF_MONTH))
                                    targetDate = localCal.timeInMillis
                                    showDatePicker = false
                                }) { Text("OK") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val amount = targetAmount.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && amount > 0) {
                        viewModel.addGoal(name, amount, targetDate)
                    }
                    showAddDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showEditGoalDialog != null) {
        val goal = showEditGoalDialog!!
        AlertDialog(
            onDismissRequest = { showEditGoalDialog = null },
            title = { Text("Edit Goal") },
            text = {
                Column {
                    OutlinedTextField(value = editGoalName, onValueChange = { editGoalName = it }, label = { Text("Goal Name") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editGoalTarget, onValueChange = { editGoalTarget = it }, label = { Text("Target Amount") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editGoalCurrent, onValueChange = { editGoalCurrent = it }, label = { Text("Current Amount") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val updatedGoal = goal.copy(
                        name = editGoalName,
                        targetAmount = editGoalTarget.toDoubleOrNull() ?: 0.0,
                        currentAmount = editGoalCurrent.toDoubleOrNull() ?: 0.0
                    )
                    viewModel.updateGoal(updatedGoal)
                    showEditGoalDialog = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditGoalDialog = null }) { Text("Cancel") }
            }
        )
    }
    
    if (goalToDelete != null) {
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text("Delete Goal") },
            text = { Text("Are you sure you want to delete this goal?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteGoal(goalToDelete!!.id)
                    goalToDelete = null
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) { Text("No") }
            }
        )
    }
    
    if (compensateGoal != null) {
        val goal = compensateGoal!!
        val remainingAmt = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)
        
        AlertDialog(
            onDismissRequest = { compensateGoal = null },
            title = { Text("Compensate from Budget") },
            text = {
                Column {
                    Text("Goal remaining: ₹${"%.2f".format(remainingAmt)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (compensateBudget == null) {
                        Text("Select a budget:")
                        val listState = androidx.compose.foundation.lazy.rememberLazyListState()
        val view = androidx.compose.ui.platform.LocalView.current
        val isHapticsEnabled = com.example.financetracker.theme.LocalHapticEnabled.current
        LaunchedEffect(listState.firstVisibleItemIndex) { if(isHapticsEnabled) view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK) }
        LazyColumn(state = listState, modifier = Modifier.height(200.dp)) {
                            val availableCats = categories.filter { !it.name.equals("Salary", ignoreCase = true) }
                            items(availableCats) { cat ->
                                val bud = currentMonthBudgets[cat]
                                if (bud != null && bud.amount > 0) {
                                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).bounceClick { compensateBudget = bud }) {
                                        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(cat.name)
                                            Text("₹${"%.2f".format(bud.amount)}")
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        val cName = categories.find { it.id == compensateBudget!!.categoryId }?.name ?: ""
                        Text("Compensating from $cName (Available: ₹${"%.2f".format(compensateBudget!!.amount)})")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = compensateAmountInput, 
                            onValueChange = { compensateAmountInput = it },
                            label = { Text("Amount to deduct from budget") }
                        )
                        val inputAmt = compensateAmountInput.toDoubleOrNull() ?: 0.0
                        val newRemaining = (remainingAmt - inputAmt).coerceAtLeast(0.0)
                        Text("Goal amount remaining after this: ₹${"%.2f".format(newRemaining)}", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        
                        TextButton(onClick = { compensateBudget = null }, modifier = Modifier.padding(top=8.dp)) {
                            Text("Change Budget")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (compensateBudget != null) {
                        val inputAmt = compensateAmountInput.toDoubleOrNull() ?: 0.0
                        if (inputAmt > 0 && inputAmt <= compensateBudget!!.amount) {
                            val newBudget = compensateBudget!!.copy(amount = compensateBudget!!.amount - inputAmt)
                            val newGoal = goal.copy(currentAmount = goal.currentAmount + inputAmt)
                            viewModel.saveBudget(newBudget)
                            viewModel.updateGoal(newGoal)
                            compensateGoal = null
                            compensateBudget = null
                            compensateAmountInput = ""
                        }
                    }
                }, enabled = compensateBudget != null && (compensateAmountInput.toDoubleOrNull() ?: 0.0) > 0) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { 
                    compensateGoal = null 
                    compensateBudget = null
                    compensateAmountInput = ""
                }) { Text("Cancel") }
            }
        )
    }

}

@Composable
fun GoalItem(goal: Goal, overallRemaining: Double, onEdit: () -> Unit, onDelete: () -> Unit, onCompensate: () -> Unit) {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = formatter.format(Date(goal.targetDate))
    
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).bounceClick(
            onClick = onEdit,
            onLongClick = onDelete
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(goal.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.bounceClick(onClick = onEdit).padding(end=8.dp), tint = Color.Gray)
            }
            Text("Target Date: $dateString", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth().height(8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Saved: ₹${"%.2f".format(goal.currentAmount)}")
                Text("Target: ₹${"%.2f".format(goal.targetAmount)}")
            }
            
            // Reallocation button, only show if target is set and we have a deficit
            if (goal.targetAmount > 0 && overallRemaining < 0 && goal.currentAmount < goal.targetAmount) {
                Button(onClick = {
                    onCompensate()
                }, modifier = Modifier.padding(top = 12.dp).fillMaxWidth()) {
                    Text("Compensate from Budgets")
                }
            }
        }
    }
}
