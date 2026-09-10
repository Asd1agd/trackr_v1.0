with open('app/src/main/java/com/example/financetracker/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace('FinanceRepository(db.transactionDao(), db.categoryDao(), db.budgetDao(), db.goalDao())', 'FinanceRepository(applicationContext, db.transactionDao(), db.categoryDao(), db.budgetDao(), db.goalDao())')

with open('app/src/main/java/com/example/financetracker/MainActivity.kt', 'w') as f:
    f.write(content)
