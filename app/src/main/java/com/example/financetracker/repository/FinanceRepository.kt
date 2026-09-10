package com.example.financetracker.repository

import android.content.Context

import androidx.glance.appwidget.updateAll
import com.example.financetracker.AllowanceWidget

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow



import com.example.financetracker.data.CategoryDao
import com.example.financetracker.data.BudgetDao
import com.example.financetracker.data.GoalDao
import com.example.financetracker.data.Budget
import com.example.financetracker.data.Goal
import com.example.financetracker.data.Category
import com.example.financetracker.data.Transaction
import com.example.financetracker.data.TransactionDao


class FinanceRepository(
    val context: Context,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao,
    private val goalDao: GoalDao
) {
    val allTransactions = transactionDao.getAllTransactions()
    val allCategories = categoryDao.getAllCategories()

    private suspend fun updateWidget() {
        try {
            AllowanceWidget().updateAll(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTransactionsBetween(start: Long, end: Long) = transactionDao.getTransactionsBetween(start, end)
    suspend fun insertTransaction(txn: Transaction) { transactionDao.insert(txn); updateWidget() }

    private val prefs: SharedPreferences = context.getSharedPreferences("finance_prefs", Context.MODE_PRIVATE)
    
    private val _essentialCategoryIds = MutableStateFlow(
        prefs.getStringSet("essential_cats", emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    )
    val essentialCategoryIds = _essentialCategoryIds.asStateFlow()

    fun updateEssentialCategories(ids: Set<Int>) {
        prefs.edit().putStringSet("essential_cats", ids.map { it.toString() }.toSet()).apply()
        _essentialCategoryIds.value = ids
    }


    val allBudgets = budgetDao.getAllBudgets()
    val allGoals = goalDao.getAllGoals()

    fun getBudgetsForMonth(month: Int, year: Int) = budgetDao.getBudgetsForMonth(month, year)

    suspend fun saveBudget(budget: Budget) {
        val existing = budgetDao.getBudget(budget.categoryId, budget.month, budget.year)
        if (existing != null) {
            budgetDao.update(budget.copy(id = existing.id))
        } else {
            budgetDao.insert(budget)
        }
    }

    
    suspend fun ensureSalaryCategoryExists() {
        val cats = categoryDao.getCategoriesSync()
        if (cats.none { it.name.equals("Salary", ignoreCase = true) }) {
            categoryDao.insert(com.example.financetracker.data.Category(name = "Salary", keywords = "salary,income,paycheck", isEssential = false))
        }
    }

    suspend fun addGoal(goal: Goal) {
        goalDao.insert(goal)
    }

    suspend fun updateGoal(goal: Goal) {
        goalDao.update(goal)
    }

    suspend fun deleteGoal(id: Int) {
        goalDao.deleteById(id)
    }


    suspend fun processNewTransaction(amount: Double, type: String, note: String, timestamp: Long, manualCategoryId: Int? = null) {
        // Deduplication check
        val timeLimit = timestamp - 30_000
        val duplicate = transactionDao.getRecentDuplicate(amount, type, timeLimit)
        if (duplicate != null) {
            return 
        }

        var finalCategoryId = manualCategoryId
        if (finalCategoryId == null) {
            // Auto-Categorization fallback
            val categories = categoryDao.getCategoriesSync()
            for (category in categories) {
                val keywords = category.keywords.split(",").map { it.trim().lowercase() }
                if (keywords.any { keyword -> note.lowercase().contains(keyword) && keyword.isNotEmpty() }) {
                    finalCategoryId = category.id
                    break
                }
            }
        }
        
        val isSubscription = note.lowercase().contains("subscription") || note.lowercase().contains("netflix") || note.lowercase().contains("spotify")

        val newTransaction = Transaction(
            amount = amount,
            type = type,
            timestamp = timestamp,
            categoryId = finalCategoryId,
            note = note,
            isSubscription = isSubscription
        )
        transactionDao.insert(newTransaction)
        updateWidget()
    }

    suspend fun addCategory(name: String) {
        categoryDao.insert(Category(name = name, keywords = ""))
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
        updateWidget()
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
        updateWidget()
    }
}
// Will overwrite using sed instead
