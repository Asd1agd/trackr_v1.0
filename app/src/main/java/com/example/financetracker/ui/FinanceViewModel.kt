package com.example.financetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.Category
import com.example.financetracker.data.Transaction
import com.example.financetracker.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.ensureSalaryCategoryExists()
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
