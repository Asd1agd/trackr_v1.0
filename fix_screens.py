import re

for filename in ['app/src/main/java/com/example/financetracker/ui/TransactionsScreen.kt', 'app/src/main/java/com/example/financetracker/ui/MainScreen.kt']:
    with open(filename, 'r') as f:
        content = f.read()
    
    if filename.endswith('TransactionsScreen.kt'):
        content = re.sub(
            r'transactionToEdit = null\s*\}\s*\)',
            r'transactionToEdit = null\n            },\n            onAddCategory = { newCategory ->\n                viewModel.addCategory(newCategory)\n            }\n        )',
            content
        )
    else:
        content = re.sub(
            r'showAddDialog = false\s*\}\s*\)',
            r'showAddDialog = false\n            },\n            onAddCategory = { newCategory ->\n                viewModel.addCategory(newCategory)\n            }\n        )',
            content
        )

    with open(filename, 'w') as f:
        f.write(content)

