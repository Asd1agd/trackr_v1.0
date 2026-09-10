with open('app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt', 'r') as f:
    content = f.read()
if "suspend fun updateGoal" not in content:
    content = content.replace(
        'suspend fun addGoal(goal: Goal) {',
        'suspend fun updateGoal(goal: com.example.financetracker.data.Goal) { goalDao.update(goal) }\n    suspend fun addGoal(goal: Goal) {'
    )
with open('app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'r') as f:
    content = f.read()
if "fun updateGoal(" not in content:
    content = content.replace(
        'fun addGoal(name: String, targetAmount: Double, targetDate: Long) {',
        'fun updateGoal(goal: com.example.financetracker.data.Goal) { viewModelScope.launch { repository.updateGoal(goal) } }\n\n    fun addGoal(name: String, targetAmount: Double, targetDate: Long) {'
    )
with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'w') as f:
    f.write(content)
