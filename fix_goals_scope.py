with open('app/src/main/java/com/example/financetracker/ui/GoalsScreen.kt', 'r') as f:
    content = f.read()

# The bad block at the end
bad_block = """    if (showEditGoalDialog != null) {
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

# Remove it from the end
content = content.replace(bad_block, '}')

# Insert it at the end of GoalsScreen
target_insertion = """            }
        )
    }"""
# In GoalsScreen, the Add Goal dialog ends with:
#             dismissButton = {
#                 TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
#             }
#         )
#     }

correct_block = """
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
"""

content = content.replace(
    '            dismissButton = {\n                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }\n            }\n        )\n    }',
    '            dismissButton = {\n                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }\n            }\n        )\n    }\n' + correct_block
)

with open('app/src/main/java/com/example/financetracker/ui/GoalsScreen.kt', 'w') as f:
    f.write(content)

