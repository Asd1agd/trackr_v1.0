package com.example.financetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE categories ADD COLUMN isEssential INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE transactions ADD COLUMN dueDate INTEGER")
        db.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `categoryId` INTEGER NOT NULL, `amount` REAL NOT NULL, `month` INTEGER NOT NULL, `year` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `goals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `targetAmount` REAL NOT NULL, `currentAmount` REAL NOT NULL, `targetDate` INTEGER NOT NULL)")
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("INSERT INTO categories (name, keywords, isEssential) VALUES ('Trip', 'hotel,flight,airbnb,make my trip,goibibo', 0)")
        db.execSQL("INSERT INTO categories (name, keywords, isEssential) VALUES ('Bike', 'petrol,service,repair,bike,fuel', 0)")
        db.execSQL("INSERT INTO categories (name, keywords, isEssential) VALUES ('Online Shopping', 'amazon,flipkart,myntra,shopping,shoes,clothes', 0)")
    }
}

@Database(entities = [Transaction::class, Category::class, Budget::class, Goal::class], version = 3, exportSchema = false)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var Instance: FinanceDatabase? = null

        fun getDatabase(context: Context): FinanceDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, FinanceDatabase::class.java, "finance_database")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("INSERT INTO categories (name, keywords, isEssential) VALUES ('Food', 'zomato,swiggy,restaurant,food,dinner,lunch,mcdonalds', 1)")
                            db.execSQL("INSERT INTO categories (name, keywords, isEssential) VALUES ('Transport', 'uber,ola,rapido,metro,bus,train,flight', 1)")
                            db.execSQL("INSERT INTO categories (name, keywords, isEssential) VALUES ('Entertainment', 'netflix,spotify,amazon prime,movie,cinema', 0)")
                            db.execSQL("INSERT INTO categories (name, keywords, isEssential) VALUES ('Utilities', 'electricity,water,internet,wifi,recharge,bill', 1)")
                            db.execSQL("INSERT INTO categories (name, keywords, isEssential) VALUES ('Trip', 'hotel,flight,airbnb,make my trip,goibibo', 0)")
                            db.execSQL("INSERT INTO categories (name, keywords, isEssential) VALUES ('Bike', 'petrol,service,repair,bike,fuel', 0)")
                            db.execSQL("INSERT INTO categories (name, keywords, isEssential) VALUES ('Online Shopping', 'amazon,flipkart,myntra,shopping,shoes,clothes', 0)")
                        }
                    })
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
