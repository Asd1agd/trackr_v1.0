with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'r') as f:
    content = f.read()

filter_code = """
    private val _selectedCategoryFilter = kotlinx.coroutines.flow.MutableStateFlow<Int?>(null)
    val selectedCategoryFilter = _selectedCategoryFilter.asStateFlow()

    fun setCategoryFilter(categoryId: Int?) {
        _selectedCategoryFilter.value = categoryId
    }
"""

content = content.replace('val categories = repository.allCategories.stateIn(', 'import kotlinx.coroutines.flow.asStateFlow\n\n' + filter_code + '\n    val categories = repository.allCategories.stateIn(')

with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'w') as f:
    f.write(content)
