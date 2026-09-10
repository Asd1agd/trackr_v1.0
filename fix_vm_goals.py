with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'r') as f:
    content = f.read()

methods = """
    fun addGoal(name: String, targetAmount: Double, targetDate: Long) {
        viewModelScope.launch {
            repository.addGoal(com.example.financetracker.data.Goal(name = name, targetAmount = targetAmount, targetDate = targetDate))
        }
    }

    fun reallocateToGoal(goal: com.example.financetracker.data.Goal) {
        viewModelScope.launch {
            val needed = goal.targetAmount - goal.currentAmount
            if (needed <= 0) return@launch
            
            // Just a simple reallocation: add 10% of needed from a simulated pool
            val newGoal = goal.copy(currentAmount = goal.currentAmount + (needed * 0.1))
            repository.updateGoal(newGoal)
        }
    }
"""

content = content.replace('fun saveBudget(budget: com.example.financetracker.data.Budget) {\n        viewModelScope.launch { repository.saveBudget(budget) }\n    }', 'fun saveBudget(budget: com.example.financetracker.data.Budget) {\n        viewModelScope.launch { repository.saveBudget(budget) }\n    }\n' + methods)

with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/financetracker/ui/GoalsScreen.kt', 'r') as f:
    goals_content = f.read()

goals_content = goals_content.replace('// viewModel.addGoal needs to be added\n                        // for now we can do a hack if it\'s not exposed', 'viewModel.addGoal(name, amount, targetDate)')
goals_content = goals_content.replace('// We will implement reallocate in ViewModel', 'viewModel.reallocateToGoal(goal)')

with open('app/src/main/java/com/example/financetracker/ui/GoalsScreen.kt', 'w') as f:
    f.write(goals_content)
