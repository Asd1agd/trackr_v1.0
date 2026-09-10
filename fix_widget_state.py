with open("app/src/main/java/com/example/financetracker/AllowanceWidget.kt", "r") as f:
    content = f.read()

bad_glance = """        provideContent {
            var todaysAllowance by remember { mutableStateOf(0.0) }
            var dailyBudgetMax by remember { mutableStateOf(1.0) }
            var essentialSpentToday by remember { mutableStateOf(0.0) }
            
            LaunchedEffect(Unit) {"""

good_glance = """
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val remainingDaysIncludingToday = (maxDays - currentDay + 1).coerceAtLeast(1)

        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfMonth = cal.timeInMillis

        val endCal = Calendar.getInstance()
        endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH))
        endCal.set(Calendar.HOUR_OF_DAY, 23)
        endCal.set(Calendar.MINUTE, 59)
        endCal.set(Calendar.SECOND, 59)
        endCal.set(Calendar.MILLISECOND, 999)
        val endOfMonth = endCal.timeInMillis

        val todayCal = Calendar.getInstance()
        todayCal.set(Calendar.HOUR_OF_DAY, 0)
        todayCal.set(Calendar.MINUTE, 0)
        todayCal.set(Calendar.SECOND, 0)
        todayCal.set(Calendar.MILLISECOND, 0)
        val todayStart = todayCal.timeInMillis

        val txns = transactionDao.getTransactionsBetween(startOfMonth, endOfMonth).first()
        val cats = categoryDao.getAllCategories().first()
        val budgets = budgetDao.getBudgetsForMonth(currentMonth, currentYear).first()

        val essentialCatIds = cats.filter { it.isEssential }.map { it.id }.toSet()
        val essentialBudget = budgets.filter { it.categoryId in essentialCatIds }.sumOf { it.amount }
        
        val essentialSpentToday = txns.filter { 
            it.timestamp >= todayStart && it.categoryId in essentialCatIds && it.type == "Debit" 
        }.sumOf { it.amount }

        var todaysAllowance = 0.0
        var dailyBudgetMax = 1.0

        if (budgets.isNotEmpty()) {
            val essentialSpentTillYesterday = txns.filter { 
                it.timestamp < todayStart && it.categoryId in essentialCatIds && it.type == "Debit" 
            }.sumOf { it.amount }

            val dailyBudget = (essentialBudget - essentialSpentTillYesterday) / remainingDaysIncludingToday
            dailyBudgetMax = dailyBudget.coerceAtLeast(1.0)
            
            todaysAllowance = dailyBudget - essentialSpentToday
        } else {
            val totalIncome = txns.filter { it.type == "Credit" }.sumOf { it.amount }
            val totalExpenses = txns.filter { it.type == "Debit" }.sumOf { it.amount }
            val remaining = totalIncome - totalExpenses
            
            val dailyBudget = remaining / remainingDaysIncludingToday
            dailyBudgetMax = dailyBudget.coerceAtLeast(1.0)
            
            todaysAllowance = remaining / remainingDaysIncludingToday
        }

        provideContent {
"""

content = content.replace(bad_glance, good_glance)

# Now we need to remove the closing brace of LaunchedEffect and the logic inside it!
# Wait, let's just replace the exact block of logic that was inside LaunchedEffect!
# Actually, I'll just rewrite AllowanceWidget.kt completely to be safe.
