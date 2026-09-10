import os

# TransactionDialog.kt
with open('app/src/main/java/com/example/financetracker/ui/TransactionDialog.kt', 'r') as f:
    dialog_content = f.read()

# Update signature to include timestamp
dialog_content = dialog_content.replace(
    'onSave: (Double, String, String, Int?) -> Unit',
    'onSave: (Double, String, String, Int?, Long) -> Unit'
)
dialog_content = dialog_content.replace(
    'onSave(parsedAmount, type, note, selectedCategoryId)',
    'onSave(parsedAmount, type, note, selectedCategoryId, selectedTimestamp)'
)

# Add timestamp state
state_block = """
    var amount by remember { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var type by remember { mutableStateOf(transaction?.type ?: "Debit") }
    var note by remember { mutableStateOf(transaction?.note ?: "") }
    var selectedCategoryId by remember { mutableStateOf(transaction?.categoryId ?: categories.firstOrNull()?.id) }
    var selectedTimestamp by remember { mutableStateOf(transaction?.timestamp ?: System.currentTimeMillis()) }
    var expanded by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedTimestamp)
"""
dialog_content = dialog_content.replace(
    """    var amount by remember { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var type by remember { mutableStateOf(transaction?.type ?: "Debit") }
    var note by remember { mutableStateOf(transaction?.note ?: "") }
    var selectedCategoryId by remember { mutableStateOf(transaction?.categoryId ?: categories.firstOrNull()?.id) }
    var expanded by remember { mutableStateOf(false) }""",
    state_block
)

# Add UI for date
ui_block = """
                Spacer(modifier = Modifier.height(8.dp))
                val dateStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(selectedTimestamp))
                OutlinedButton(onClick = { showDatePicker = true }) {
                    Text("Date: $dateStr")
                }
                
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                selectedTimestamp = datePickerState.selectedDateMillis ?: selectedTimestamp
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
"""

dialog_content = dialog_content.replace(
    'Spacer(modifier = Modifier.height(8.dp))\n                TextField(value = note, onValueChange = { note = it }, label = { Text("Note") })',
    ui_block + '\n                Spacer(modifier = Modifier.height(8.dp))\n                TextField(value = note, onValueChange = { note = it }, label = { Text("Note") })'
)

with open('app/src/main/java/com/example/financetracker/ui/TransactionDialog.kt', 'w') as f:
    f.write(dialog_content)

# MainScreen.kt
with open('app/src/main/java/com/example/financetracker/ui/MainScreen.kt', 'r') as f:
    ms_content = f.read()

ms_content = ms_content.replace(
    'onSave = { amount, type, note, catId ->',
    'onSave = { amount, type, note, catId, timestamp ->'
)
ms_content = ms_content.replace(
    'viewModel.addManualTransactionWithCategory(amount, type, note, System.currentTimeMillis(), catId)',
    'viewModel.addManualTransactionWithCategory(amount, type, note, timestamp, catId)'
)
with open('app/src/main/java/com/example/financetracker/ui/MainScreen.kt', 'w') as f:
    f.write(ms_content)

# TransactionsScreen.kt
with open('app/src/main/java/com/example/financetracker/ui/TransactionsScreen.kt', 'r') as f:
    ts_content = f.read()

ts_content = ts_content.replace(
    'onSave = { amount, type, note, catId ->',
    'onSave = { amount, type, note, catId, timestamp ->'
)
ts_content = ts_content.replace(
    'categoryId = catId',
    'categoryId = catId,\n                        timestamp = timestamp'
)
with open('app/src/main/java/com/example/financetracker/ui/TransactionsScreen.kt', 'w') as f:
    f.write(ts_content)

