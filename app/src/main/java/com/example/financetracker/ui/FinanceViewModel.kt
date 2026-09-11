package com.example.financetracker.ui
import com.example.financetracker.theme.bounceClick

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.Category
import com.example.financetracker.data.Transaction
import com.example.financetracker.repository.FinanceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    init {
        checkAndInjectMonthlySalary()
    }

    fun checkAndInjectMonthlySalary() {
        // Check and inject monthly salary
        viewModelScope.launch {
            val prefs = repository.context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
            val salaryStr = prefs.getString("salary_per_month", "")
            if (!salaryStr.isNullOrEmpty()) {
                val salaryAmount = salaryStr.toDoubleOrNull() ?: 0.0
                if (salaryAmount > 0) {
                    val cal = java.util.Calendar.getInstance()
                    val currentMonth = cal.get(java.util.Calendar.MONTH)
                    val currentYear = cal.get(java.util.Calendar.YEAR)
                    cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
                    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    cal.set(java.util.Calendar.MINUTE, 0)
                    cal.set(java.util.Calendar.SECOND, 0)
                    cal.set(java.util.Calendar.MILLISECOND, 0)
                    val firstDayTs = cal.timeInMillis

                    repository.ensureSalaryCategoryExists()
                    val cats = repository.allCategories.first()
                    val salaryCat = cats.find { it.name.equals("Salary", ignoreCase = true) }
                    val salaryCatId = salaryCat?.id ?: 0

                    val txns = repository.getTransactionsBetween(firstDayTs, firstDayTs + 86400000L * 31).first()
                    val existingSalaryTxn = txns.find { it.type == "Credit" && it.note == "Monthly Salary" }

                    if (existingSalaryTxn != null) {
                        if (existingSalaryTxn.amount != salaryAmount) {
                            repository.updateTransaction(existingSalaryTxn.copy(amount = salaryAmount, categoryId = salaryCatId))
                        }
                    } else {
                        repository.processNewTransaction(
                            amount = salaryAmount,
                            type = "Credit",
                            note = "Monthly Salary",
                            timestamp = firstDayTs,
                            manualCategoryId = salaryCatId
                        )
                    }
                }
            }
        }

    }

    val transactions: kotlinx.coroutines.flow.StateFlow<List<com.example.financetracker.data.Transaction>> = repository.allTransactions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    


    private val _selectedCategoryFilter = kotlinx.coroutines.flow.MutableStateFlow<Int?>(null)
    val selectedCategoryFilter = _selectedCategoryFilter.asStateFlow()

    
    val essentialCategoryIds = repository.essentialCategoryIds

    fun updateEssentialCategories(ids: Set<Int>) {
        repository.updateEssentialCategories(ids)
    }

    fun setCategoryFilter(categoryId: Int?) {
        _selectedCategoryFilter.value = categoryId
    }

    
    val budgets: kotlinx.coroutines.flow.StateFlow<List<com.example.financetracker.data.Budget>> = repository.allBudgets.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val goals: kotlinx.coroutines.flow.StateFlow<List<com.example.financetracker.data.Goal>> = repository.allGoals.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    fun saveBudget(budget: com.example.financetracker.data.Budget) {
        viewModelScope.launch { repository.saveBudget(budget) }
    }

    fun updateGoal(goal: com.example.financetracker.data.Goal) { viewModelScope.launch { repository.updateGoal(goal) } }

    fun addGoal(name: String, targetAmount: Double, targetDate: Long, currentAmount: Double = 0.0) {
        viewModelScope.launch {
            repository.addGoal(com.example.financetracker.data.Goal(name = name, targetAmount = targetAmount, currentAmount = currentAmount, targetDate = targetDate))
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


    val categories: kotlinx.coroutines.flow.StateFlow<List<com.example.financetracker.data.Category>> = repository.allCategories.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addManualTransactionWithCategory(amount: Double, type: String, note: String, timestamp: Long, categoryId: Int?) {
        viewModelScope.launch {
            repository.processNewTransaction(amount, type, note, timestamp, categoryId)
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            repository.addCategory(name)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }
    private val _importLogs = MutableStateFlow<List<com.example.financetracker.data.ImportLog>>(emptyList())
    val importLogs = _importLogs.asStateFlow()
    
    init {
        viewModelScope.launch { repository.allImportLogs.collect { _importLogs.value = it } }
    }

    fun deleteImportLog(id: Int) {
        viewModelScope.launch {
            repository.deleteImportLog(id)
        }
    }

    suspend fun importYamlData(yamlText: String, filename: String): Boolean {
        try {
            val map = org.yaml.snakeyaml.Yaml().load<Map<String, Any>>(yamlText)
            
            // Validation
            if (map == null || (!map.containsKey("transactions") && !map.containsKey("goals") && !map.containsKey("budgets"))) {
                return false
            }

            val importId = repository.insertImportLog(filename, System.currentTimeMillis())
            
            val txns = map["transactions"] as? List<Map<String, Any>>
            txns?.forEach { tMap ->
                val amount = (tMap["amount"] as? Number)?.toDouble() ?: 0.0
                val type = (tMap["type"] as? String) ?: "Debit"
                val note = (tMap["note"] as? String) ?: ""
                val catName = (tMap["category"] as? String) ?: "Other"
                val ts = (tMap["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                
                var cat = categories.value.find { it.name.equals(catName, ignoreCase = true) }
                if (cat == null) {
                    addCategory(catName)
                    kotlinx.coroutines.delay(100) // wait for DB insert
                    cat = categories.value.find { it.name.equals(catName, ignoreCase = true) }
                }
                
                val finalCategoryId = cat?.id
                val isSubscription = note.lowercase().contains("subscription") || note.lowercase().contains("netflix") || note.lowercase().contains("spotify")
                val txn = com.example.financetracker.data.Transaction(
                    amount = amount,
                    type = type,
                    timestamp = ts,
                    categoryId = finalCategoryId,
                    note = note,
                    isSubscription = isSubscription,
                    importId = importId
                )
                repository.insertTransaction(txn)
            }
            
            val gls = map["goals"] as? List<Map<String, Any>>
            gls?.forEach { gMap ->
                val name = (gMap["name"] as? String) ?: "Unnamed"
                val targetAmt = (gMap["targetAmount"] as? Number)?.toDouble() ?: 0.0
                val savedAmt = (gMap["savedAmount"] as? Number)?.toDouble() ?: 0.0
                val targetDate = (gMap["targetDate"] as? Number)?.toLong() ?: 0L
                repository.addGoal(com.example.financetracker.data.Goal(name = name, targetAmount = targetAmt, targetDate = targetDate, currentAmount = savedAmt, importId = importId))
            }
            
            val bdgts = map["budgets"] as? List<Map<String, Any>>
            bdgts?.forEach { bMap ->
                val catName = (bMap["category"] as? String) ?: "Other"
                val amount = (bMap["amount"] as? Number)?.toDouble() ?: 0.0
                val month = (bMap["month"] as? Number)?.toInt() ?: java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
                val year = (bMap["year"] as? Number)?.toInt() ?: java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                
                var cat = categories.value.find { it.name.equals(catName, ignoreCase = true) }
                if (cat == null) {
                    addCategory(catName)
                    kotlinx.coroutines.delay(100)
                    cat = categories.value.find { it.name.equals(catName, ignoreCase = true) }
                }
                cat?.let { c ->
                    repository.saveBudget(com.example.financetracker.data.Budget(categoryId = c.id, amount = amount, month = month, year = year, importId = importId))
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}

class FinanceViewModelFactory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
