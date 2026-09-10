with open("app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt", "r") as f:
    content = f.read()

# Add imports
imports = """
import androidx.glance.appwidget.updateAll
import com.example.financetracker.AllowanceWidget
"""
content = content.replace("import android.content.Context", "import android.content.Context\n" + imports)

# We need to add the update function
widget_update = """
    private suspend fun updateWidget() {
        try {
            AllowanceWidget().updateAll(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
"""
content = content.replace("class FinanceRepository(", widget_update + "\nclass FinanceRepository(")

# Call updateWidget after insert, update, delete
content = content.replace("transactionDao.insert(newTransaction)", "transactionDao.insert(newTransaction)\n        updateWidget()")
content = content.replace("transactionDao.update(transaction)", "transactionDao.update(transaction)\n        updateWidget()")
content = content.replace("transactionDao.delete(transaction)", "transactionDao.delete(transaction)\n        updateWidget()")
content = content.replace("suspend fun insertTransaction(txn: Transaction) = transactionDao.insert(txn)", "suspend fun insertTransaction(txn: Transaction) { transactionDao.insert(txn); updateWidget() }")


with open("app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt", "w") as f:
    f.write(content)
