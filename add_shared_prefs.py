with open('app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt', 'r') as f:
    content = f.read()

prefs = """
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

"""
content = prefs + content

content = content.replace(
    'class FinanceRepository(',
    'class FinanceRepository(\n    private val context: Context,'
)

methods = """
    private val prefs: SharedPreferences = context.getSharedPreferences("finance_prefs", Context.MODE_PRIVATE)
    
    private val _essentialCategoryIds = MutableStateFlow(
        prefs.getStringSet("essential_cats", emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    )
    val essentialCategoryIds = _essentialCategoryIds.asStateFlow()

    fun updateEssentialCategories(ids: Set<Int>) {
        prefs.edit().putStringSet("essential_cats", ids.map { it.toString() }.toSet()).apply()
        _essentialCategoryIds.value = ids
    }
"""

content = content.replace('val allCategories = categoryDao.getAllCategories()', 'val allCategories = categoryDao.getAllCategories()\n' + methods)

with open('app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt', 'w') as f:
    f.write(content)
