sed -i 's/version = 3/version = 4/' app/src/main/java/com/example/financetracker/data/FinanceDatabase.kt
sed -i 's/Goal::class\]/Goal::class, ImportLog::class\]/' app/src/main/java/com/example/financetracker/data/FinanceDatabase.kt
sed -i '/abstract fun goalDao(): GoalDao/a \    abstract fun importLogDao(): ImportLogDao' app/src/main/java/com/example/financetracker/data/FinanceDatabase.kt
sed -i '/.addMigrations(MIGRATION_1_2, MIGRATION_2_3)/s/MIGRATION_2_3/MIGRATION_2_3, MIGRATION_3_4/' app/src/main/java/com/example/financetracker/data/FinanceDatabase.kt

cat << 'INNER_EOF' >> add_migration.txt

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE transactions ADD COLUMN importId INTEGER DEFAULT NULL")
        db.execSQL("ALTER TABLE goals ADD COLUMN importId INTEGER DEFAULT NULL")
        db.execSQL("ALTER TABLE budgets ADD COLUMN importId INTEGER DEFAULT NULL")
        db.execSQL("CREATE TABLE IF NOT EXISTS `import_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `filename` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
    }
}
INNER_EOF

sed -i '/val MIGRATION_2_3/e cat add_migration.txt' app/src/main/java/com/example/financetracker/data/FinanceDatabase.kt
