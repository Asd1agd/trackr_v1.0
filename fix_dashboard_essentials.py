import re

with open('app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt', 'r') as f:
    content = f.read()

# Add essentialCategoryIds state
content = content.replace(
    'val budgets by viewModel.budgets.collectAsState()',
    'val budgets by viewModel.budgets.collectAsState()\n    val selectedEssentialCategoryIds by viewModel.essentialCategoryIds.collectAsState(initial = emptySet())'
)

# Update essentialCatIds calculation
content = content.replace(
    'val essentialCatIds = categories.filter { it.isEssential }.map { it.id }',
    'val essentialCatIds = if (selectedEssentialCategoryIds.isEmpty()) categories.filter { it.isEssential }.map { it.id } else selectedEssentialCategoryIds.toList()'
)

# Add dialog state
content = content.replace(
    'var showDateRangePicker by remember { mutableStateOf(false) }',
    'var showDateRangePicker by remember { mutableStateOf(false) }\n    var showEssentialCatsDialog by remember { mutableStateOf(false) }'
)

# Make Card clickable
content = content.replace(
    'modifier = Modifier.fillMaxWidth().padding(16.dp),',
    'modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { showEssentialCatsDialog = true },'
)

# Add the dialog at the end
dialog_code = """
    if (showEssentialCatsDialog) {
        var tempSelected by remember { mutableStateOf(essentialCatIds.toSet()) }
        AlertDialog(
            onDismissRequest = { showEssentialCatsDialog = false },
            title = { Text("Select Everyday Essentials") },
            text = {
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    categories.forEach { cat ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable {
                            tempSelected = if (tempSelected.contains(cat.id)) tempSelected - cat.id else tempSelected + cat.id
                        }.padding(vertical = 4.dp)) {
                            Checkbox(checked = tempSelected.contains(cat.id), onCheckedChange = { 
                                tempSelected = if (it) tempSelected + cat.id else tempSelected - cat.id
                            })
                            Text(cat.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateEssentialCategories(tempSelected)
                    showEssentialCatsDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEssentialCatsDialog = false }) { Text("Cancel") }
            }
        )
    }
}"""

content = re.sub(r'\}\n$', dialog_code, content)

with open('app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt', 'w') as f:
    f.write(content)

