import os

def write_file(path, content):
    with open(path, 'w') as f:
        f.write(content.strip() + '\n')

base_dir = 'app/src/main/java/com/example/financetracker/data'

# Update Category
with open(f'{base_dir}/Category.kt', 'r') as f:
    cat_content = f.read()
cat_content = cat_content.replace('val keywords: String // Comma separated, e.g. "Zomato,Uber"', 'val keywords: String, // Comma separated, e.g. "Zomato,Uber"\n    val isEssential: Boolean = false')
write_file(f'{base_dir}/Category.kt', cat_content)

# Update Transaction
with open(f'{base_dir}/Transaction.kt', 'r') as f:
    txn_content = f.read()
txn_content = txn_content.replace('val isSubscription: Boolean = false', 'val isSubscription: Boolean = false,\n    val dueDate: Long? = null')
write_file(f'{base_dir}/Transaction.kt', txn_content)

# Create Budget
write_file(f'{base_dir}/Budget.kt', """
package com.example.financetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val amount: Double,
    val month: Int,
    val year: Int
)
""")

# Create Goal
write_file(f'{base_dir}/Goal.kt', """
package com.example.financetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: Long
)
""")

# Create BudgetDao
write_file(f'{base_dir}/BudgetDao.kt', """
package com.example.financetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year")
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)
    
    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND month = :month AND year = :year LIMIT 1")
    suspend fun getBudget(categoryId: Int, month: Int, year: Int): Budget?
}
""")

# Create GoalDao
write_file(f'{base_dir}/GoalDao.kt', """
package com.example.financetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<Goal>>

    @Insert
    suspend fun insert(goal: Goal)

    @Update
    suspend fun update(goal: Goal)
    
    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteById(id: Int)
}
""")

# Update FinanceDatabase
with open(f'{base_dir}/FinanceDatabase.kt', 'r') as f:
    db_content = f.read()

migration_3 = """
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE categories ADD COLUMN isEssential INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE transactions ADD COLUMN dueDate INTEGER")
        db.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `categoryId` INTEGER NOT NULL, `amount` REAL NOT NULL, `month` INTEGER NOT NULL, `year` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `goals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `targetAmount` REAL NOT NULL, `currentAmount` REAL NOT NULL, `targetDate` INTEGER NOT NULL)")
    }
}
"""

db_content = db_content.replace('val MIGRATION_1_2', migration_3 + '\nval MIGRATION_1_2')
db_content = db_content.replace('version = 2', 'version = 3')
db_content = db_content.replace('[Transaction::class, Category::class]', '[Transaction::class, Category::class, Budget::class, Goal::class]')
db_content = db_content.replace('abstract fun categoryDao(): CategoryDao', 'abstract fun categoryDao(): CategoryDao\n    abstract fun budgetDao(): BudgetDao\n    abstract fun goalDao(): GoalDao')
db_content = db_content.replace('.addMigrations(MIGRATION_1_2)', '.addMigrations(MIGRATION_1_2, MIGRATION_2_3)')

# Note: We should also update the category seeding in fallbackToDestructiveMigration if it recreates DB
db_content = db_content.replace(
    "INSERT INTO categories (name, keywords) VALUES ('Food', 'zomato,swiggy,restaurant,food,dinner,lunch,mcdonalds')",
    "INSERT INTO categories (name, keywords, isEssential) VALUES ('Food', 'zomato,swiggy,restaurant,food,dinner,lunch,mcdonalds', 1)"
)
db_content = db_content.replace(
    "INSERT INTO categories (name, keywords) VALUES ('Transport', 'uber,ola,rapido,metro,bus,train,flight')",
    "INSERT INTO categories (name, keywords, isEssential) VALUES ('Transport', 'uber,ola,rapido,metro,bus,train,flight', 1)"
)
db_content = db_content.replace(
    "INSERT INTO categories (name, keywords) VALUES ('Utilities', 'electricity,water,internet,wifi,recharge,bill')",
    "INSERT INTO categories (name, keywords, isEssential) VALUES ('Utilities', 'electricity,water,internet,wifi,recharge,bill', 1)"
)


write_file(f'{base_dir}/FinanceDatabase.kt', db_content)
