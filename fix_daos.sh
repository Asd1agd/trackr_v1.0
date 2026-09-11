# Fix TransactionDao
sed -i '/@Query("DELETE FROM transactions WHERE importId = :importId")/d' app/src/main/java/com/example/financetracker/data/TransactionDao.kt
sed -i '/suspend fun deleteByImportId(importId: Int)/d' app/src/main/java/com/example/financetracker/data/TransactionDao.kt
sed -i '/interface TransactionDao {/a \    @Query("DELETE FROM transactions WHERE importId = :importId")\n    suspend fun deleteByImportId(importId: Int)' app/src/main/java/com/example/financetracker/data/TransactionDao.kt

# Fix GoalDao
sed -i '/@Query("DELETE FROM goals WHERE importId = :importId")/d' app/src/main/java/com/example/financetracker/data/GoalDao.kt
sed -i '/suspend fun deleteByImportId(importId: Int)/d' app/src/main/java/com/example/financetracker/data/GoalDao.kt
sed -i '/interface GoalDao {/a \    @Query("DELETE FROM goals WHERE importId = :importId")\n    suspend fun deleteByImportId(importId: Int)' app/src/main/java/com/example/financetracker/data/GoalDao.kt

# Fix BudgetDao
sed -i '/@Query("DELETE FROM budgets WHERE importId = :importId")/d' app/src/main/java/com/example/financetracker/data/BudgetDao.kt
sed -i '/suspend fun deleteByImportId(importId: Int)/d' app/src/main/java/com/example/financetracker/data/BudgetDao.kt
sed -i '/interface BudgetDao {/a \    @Query("DELETE FROM budgets WHERE importId = :importId")\n    suspend fun deleteByImportId(importId: Int)' app/src/main/java/com/example/financetracker/data/BudgetDao.kt
