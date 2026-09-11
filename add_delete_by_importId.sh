sed -i '/@Dao/a \
    @Query("DELETE FROM transactions WHERE importId = :importId")\
    suspend fun deleteByImportId(importId: Int)' app/src/main/java/com/example/financetracker/data/TransactionDao.kt

sed -i '/@Dao/a \
    @Query("DELETE FROM goals WHERE importId = :importId")\
    suspend fun deleteByImportId(importId: Int)' app/src/main/java/com/example/financetracker/data/GoalDao.kt

sed -i '/@Dao/a \
    @Query("DELETE FROM budgets WHERE importId = :importId")\
    suspend fun deleteByImportId(importId: Int)' app/src/main/java/com/example/financetracker/data/BudgetDao.kt
