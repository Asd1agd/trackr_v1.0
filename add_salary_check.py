with open("app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt", "r") as f:
    content = f.read()

injection = """
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
                    val firstDayTs = cal.timeInMillis

                    val txns = repository.getTransactionsBetween(firstDayTs, firstDayTs + 86400000L * 31).first()
                    val salaryExists = txns.any { it.amount == salaryAmount && it.type == "Credit" && it.note == "Monthly Salary" }

                    if (!salaryExists) {
                        repository.insertTransaction(com.example.financetracker.data.Transaction(
                            amount = salaryAmount,
                            type = "Credit",
                            note = "Monthly Salary",
                            timestamp = firstDayTs,
                            categoryId = 0
                        ))
                    }
                }
            }
        }
"""

content = content.replace("init {\n        viewModelScope.launch {", "init {\n" + injection + "\n        viewModelScope.launch {")

# add imports
imports = "import kotlinx.coroutines.flow.first\n"
content = content.replace("import kotlinx.coroutines.flow.SharingStarted", imports + "import kotlinx.coroutines.flow.SharingStarted")

with open("app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt", "w") as f:
    f.write(content)
