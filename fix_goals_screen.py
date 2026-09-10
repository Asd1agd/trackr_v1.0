import re

with open('app/src/main/java/com/example/financetracker/ui/GoalsScreen.kt', 'r') as f:
    content = f.read()

# Make sure imports are present
if "import java.util.Calendar" not in content:
    content = content.replace('import java.util.Date', 'import java.util.Date\nimport java.util.Calendar')

# Add necessary states for editing goal
state_additions = """
    var showEditGoalDialog by remember { mutableStateOf<com.example.financetracker.data.Goal?>(null) }
    var editGoalName by remember { mutableStateOf("") }
    var editGoalTarget by remember { mutableStateOf("") }
    var editGoalCurrent by remember { mutableStateOf("") }
"""

content = content.replace('var showAddGoalDialog by remember { mutableStateOf(false) }', 'var showAddGoalDialog by remember { mutableStateOf(false) }\n' + state_additions)

# Bring in Salary and Budget logic to Goals Screen
top_logic = """
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val allBudgets by viewModel.budgets.collectAsState()
    
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
    
    val totalBudget = categories.sumOf { cat ->
        val explicit = allBudgets.find { it.categoryId == cat.id && it.month == currentMonth && it.year == currentYear }
        if (explicit != null) explicit.amount else (allBudgets.filter { it.categoryId == cat.id && (it.year < currentYear || (it.year == currentYear && it.month < currentMonth)) }.maxByOrNull { it.year * 12 + it.month }?.amount ?: 0.0)
    }
    
    val currentTimestamp = System.currentTimeMillis()
    val totalGoalsMonthlyRequirement = goals.sumOf { goal ->
        val remainingAmount = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)
        val remainingMonths = ((goal.targetDate - currentTimestamp) / (1000L * 60 * 60 * 24 * 30)).coerceAtLeast(1).toInt()
        remainingAmount / remainingMonths
    }
    
    val overallRemaining = currentSalary - totalBudget - totalGoalsMonthlyRequirement
"""

content = content.replace(
    'Column(modifier = modifier.fillMaxSize()) {',
    top_logic + '\n    Column(modifier = modifier.fillMaxSize()) {'
)

# Replace the top text in GoalsScreen
new_top_card = """
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 12.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Salary Remaining (After Budgets & Goals)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                Text(text = "₹${"%.2f".format(overallRemaining)}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = if (overallRemaining >= 0) MaterialTheme.colorScheme.primary else Color.Red)
                Text("Goals Req/mo: ₹${"%.2f".format(totalGoalsMonthlyRequirement)}", fontSize = 12.sp)
            }
        }
"""
content = content.replace(
    'Text("Your Financial Goals", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))',
    new_top_card + '\n        Text("Your Financial Goals", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))'
)

# Make Goal items editable
content = content.replace(
    'Card(\n        modifier = modifier',
    'Card(\n        modifier = modifier.clickable { showEditGoalDialog = goal; editGoalName = goal.name; editGoalTarget = goal.targetAmount.toString(); editGoalCurrent = goal.currentAmount.toString() }'
)

# Add remaining months text to Goal item
goal_item_math = """
    val remainingMonths = ((goal.targetDate - System.currentTimeMillis()) / (1000L * 60 * 60 * 24 * 30)).coerceAtLeast(1).toInt()
    val monthlyReq = ((goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)) / remainingMonths
"""
content = content.replace(
    'val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat() else 0f',
    goal_item_math + '\n    val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat() else 0f'
)
content = content.replace(
    'Text("Target Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(goal.targetDate))}", fontSize = 12.sp, color = Color.Gray)',
    'Text("Target Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(goal.targetDate))} (₹${"%.2f".format(monthlyReq)}/mo)", fontSize = 12.sp, color = Color.Gray)'
)

# Add Edit Dialog
edit_dialog_code = """
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
}"""

content = re.sub(r'\}\n$', edit_dialog_code, content)

with open('app/src/main/java/com/example/financetracker/ui/GoalsScreen.kt', 'w') as f:
    f.write(content)

