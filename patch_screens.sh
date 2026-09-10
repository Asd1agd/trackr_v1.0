sed -i 's/transactionToEdit = null\n            }/transactionToEdit = null\n            },\n            onAddCategory = { newCategory ->\n                viewModel.addCategory(newCategory)\n            }/' app/src/main/java/com/example/financetracker/ui/TransactionsScreen.kt

sed -i 's/showAddDialog = false\n            }/showAddDialog = false\n            },\n            onAddCategory = { newCategory ->\n                viewModel.addCategory(newCategory)\n            }/' app/src/main/java/com/example/financetracker/ui/MainScreen.kt
