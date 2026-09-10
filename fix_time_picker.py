import os

with open('app/src/main/java/com/example/financetracker/ui/TransactionDialog.kt', 'r') as f:
    content = f.read()

state_block = """
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedTimestamp)
    
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = java.util.Calendar.getInstance().apply { timeInMillis = selectedTimestamp }.get(java.util.Calendar.HOUR_OF_DAY),
        initialMinute = java.util.Calendar.getInstance().apply { timeInMillis = selectedTimestamp }.get(java.util.Calendar.MINUTE)
    )
"""

content = content.replace(
    'var showDatePicker by remember { mutableStateOf(false) }\n    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedTimestamp)',
    state_block
)

ui_block = """
                Spacer(modifier = Modifier.height(8.dp))
                val dateStr = java.text.SimpleDateFormat("MMM dd, yyyy - hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(selectedTimestamp))
                OutlinedButton(onClick = { showDatePicker = true }) {
                    Text("Date & Time: $dateStr")
                }
                
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val cal = java.util.Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
                                val newDateCal = java.util.Calendar.getInstance().apply { timeInMillis = datePickerState.selectedDateMillis ?: selectedTimestamp }
                                cal.set(java.util.Calendar.YEAR, newDateCal.get(java.util.Calendar.YEAR))
                                cal.set(java.util.Calendar.MONTH, newDateCal.get(java.util.Calendar.MONTH))
                                cal.set(java.util.Calendar.DAY_OF_MONTH, newDateCal.get(java.util.Calendar.DAY_OF_MONTH))
                                selectedTimestamp = cal.timeInMillis
                                showDatePicker = false
                                showTimePicker = true // Chain to time picker
                            }) { Text("Next") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                
                if (showTimePicker) {
                    AlertDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val cal = java.util.Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
                                cal.set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                                cal.set(java.util.Calendar.MINUTE, timePickerState.minute)
                                selectedTimestamp = cal.timeInMillis
                                showTimePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                        },
                        text = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                TimePicker(state = timePickerState)
                            }
                        }
                    )
                }
"""

content = content.replace(
    'Spacer(modifier = Modifier.height(8.dp))\n                val dateStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(selectedTimestamp))',
    '// Replaced'
)

# Need to find the exact block to replace for UI. I'll use a regex or string replacement.
import re
content = re.sub(r'OutlinedButton\(onClick = \{ showDatePicker = true \}\) \{[\s\S]*?DatePicker\(state = datePickerState\)\n                    \}\n                \}', ui_block, content)

with open('app/src/main/java/com/example/financetracker/ui/TransactionDialog.kt', 'w') as f:
    f.write(content)
