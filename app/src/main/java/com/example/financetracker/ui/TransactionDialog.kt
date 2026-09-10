package com.example.financetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.data.Category
import com.example.financetracker.data.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDialog(
    transaction: Transaction?, 
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSwitchToYaml: (() -> Unit)? = null,
    onSave: (Double, String, String, Int?, Long) -> Unit,
    onAddCategory: (String) -> Unit
) {

    var amount by remember { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var type by remember { mutableStateOf(transaction?.type ?: "Debit") }
    var note by remember { mutableStateOf(transaction?.note ?: "") }
    var selectedCategoryId by remember { mutableStateOf(transaction?.categoryId ?: categories.firstOrNull()?.id) }
    var selectedTimestamp by remember { mutableStateOf(transaction?.timestamp ?: System.currentTimeMillis()) }
    var expanded by remember { mutableStateOf(false) }

    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedTimestamp)
    
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = java.util.Calendar.getInstance().apply { timeInMillis = selectedTimestamp }.get(java.util.Calendar.HOUR_OF_DAY),
        initialMinute = java.util.Calendar.getInstance().apply { timeInMillis = selectedTimestamp }.get(java.util.Calendar.MINUTE)
    )



    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add Custom Category") },
            text = {
                TextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        onAddCategory(newCategoryName)
                        showAddCategoryDialog = false
                        newCategoryName = ""
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancel") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(if (transaction == null) "New Transaction" else "Edit Transaction")
                if (onSwitchToYaml != null) {
                    TextButton(onClick = onSwitchToYaml) {
                        Text("YAML Input")
                    }
                }
            }
        },
        text = {
            Column {
                TextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    FilterChip(selected = type == "Credit", onClick = { type = "Credit" }, label = { Text("Credit") })
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(selected = type == "Debit", onClick = { type = "Debit" }, label = { Text("Debit") })
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        val selectedName = categories.find { it.id == selectedCategoryId }?.name ?: "Select Category"
                        TextField(
                            value = selectedName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategoryId = category.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    IconButton(onClick = { showAddCategoryDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Category")
                    }
                }

                
                // Replaced
                
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
                                val newDateCal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply { timeInMillis = datePickerState.selectedDateMillis ?: selectedTimestamp }
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


                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = note, onValueChange = { note = it }, label = { Text("Note") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                onSave(parsedAmount, type, note, selectedCategoryId, selectedTimestamp)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
