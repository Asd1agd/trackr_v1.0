with open('app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt', 'r') as f:
    content = f.read()

methods = """
    suspend fun ensureSalaryCategoryExists() {
        val cats = categoryDao.getCategoriesSync()
        if (cats.none { it.name.equals("Salary", ignoreCase = true) }) {
            categoryDao.insert(com.example.financetracker.data.Category(name = "Salary", keywords = "salary,income,paycheck", isEssential = false))
        }
    }
"""
content = content.replace('suspend fun addGoal(goal: Goal) {', methods + '\n    suspend fun addGoal(goal: Goal) {')

with open('app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'r') as f:
    vm_content = f.read()

init_block = """
    init {
        viewModelScope.launch {
            repository.ensureSalaryCategoryExists()
        }
    }
"""

vm_content = vm_content.replace('class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {', 'class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {\n' + init_block)

with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'w') as f:
    f.write(vm_content)
