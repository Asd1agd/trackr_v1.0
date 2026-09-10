import re

# Fix FinanceViewModel explicit types
with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'r') as f:
    vm = f.read()
vm = vm.replace('val transactions = repository.allTransactions.stateIn(', 'val transactions: kotlinx.coroutines.flow.StateFlow<List<com.example.financetracker.data.Transaction>> = repository.allTransactions.stateIn(')
vm = vm.replace('val budgets = repository.allBudgets.stateIn(', 'val budgets: kotlinx.coroutines.flow.StateFlow<List<com.example.financetracker.data.Budget>> = repository.allBudgets.stateIn(')
vm = vm.replace('val goals = repository.allGoals.stateIn(', 'val goals: kotlinx.coroutines.flow.StateFlow<List<com.example.financetracker.data.Goal>> = repository.allGoals.stateIn(')
vm = vm.replace('val categories = repository.allCategories.stateIn(', 'val categories: kotlinx.coroutines.flow.StateFlow<List<com.example.financetracker.data.Category>> = repository.allCategories.stateIn(')
with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'w') as f:
    f.write(vm)

# Fix MainScreen collectAsState imports
with open('app/src/main/java/com/example/financetracker/ui/MainScreen.kt', 'r') as f:
    main_sc = f.read()
if 'import androidx.compose.runtime.collectAsState' not in main_sc:
    main_sc = main_sc.replace('import androidx.compose.runtime.*', 'import androidx.compose.runtime.*\nimport androidx.compose.runtime.collectAsState')
with open('app/src/main/java/com/example/financetracker/ui/MainScreen.kt', 'w') as f:
    f.write(main_sc)

# Fix TransactionsScreen collectAsState imports
with open('app/src/main/java/com/example/financetracker/ui/TransactionsScreen.kt', 'r') as f:
    tx_sc = f.read()
if 'import androidx.compose.runtime.collectAsState' not in tx_sc:
    tx_sc = tx_sc.replace('import androidx.compose.runtime.*', 'import androidx.compose.runtime.*\nimport androidx.compose.runtime.collectAsState')
with open('app/src/main/java/com/example/financetracker/ui/TransactionsScreen.kt', 'w') as f:
    f.write(tx_sc)

# Revert and properly rewrite GoalsScreen.kt
with open('app/src/main/java/com/example/financetracker/ui/GoalsScreen.kt', 'r') as f:
    goals = f.read()
if 'import androidx.compose.runtime.collectAsState' not in goals:
    goals = goals.replace('import androidx.compose.runtime.*', 'import androidx.compose.runtime.*\nimport androidx.compose.runtime.collectAsState')
with open('app/src/main/java/com/example/financetracker/ui/GoalsScreen.kt', 'w') as f:
    f.write(goals)

