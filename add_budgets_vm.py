with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'r') as f:
    content = f.read()

budgets_code = """
    val budgets = repository.allBudgets.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val goals = repository.allGoals.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    fun saveBudget(budget: com.example.financetracker.data.Budget) {
        viewModelScope.launch { repository.saveBudget(budget) }
    }
"""

content = content.replace('val categories = repository.allCategories.stateIn(', budgets_code + '\n    val categories = repository.allCategories.stateIn(')

with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'w') as f:
    f.write(content)
