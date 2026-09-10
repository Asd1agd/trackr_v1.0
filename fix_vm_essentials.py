with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'r') as f:
    content = f.read()

methods = """
    val essentialCategoryIds = repository.essentialCategoryIds

    fun updateEssentialCategories(ids: Set<Int>) {
        repository.updateEssentialCategories(ids)
    }
"""

content = content.replace('fun setCategoryFilter(categoryId: Int?) {', methods + '\n    fun setCategoryFilter(categoryId: Int?) {')

with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'w') as f:
    f.write(content)
