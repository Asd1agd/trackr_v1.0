with open("app/src/main/java/com/example/financetracker/AllowanceWidget.kt", "r") as f:
    content = f.read()

bad_logic = """                val essentialBudget = budgets.filter { it.categoryId in essentialCatIds }.sumOf { it.amount }

                val essentialSpentTillYesterday = txns.filter { 
                    it.timestamp < todayStart && it.categoryId in essentialCatIds && it.type == "Debit" 
                }.sumOf { it.amount }

                val dailyBudget = (essentialBudget - essentialSpentTillYesterday) / remainingDaysIncludingToday
                dailyBudgetMax = dailyBudget.coerceAtLeast(1.0)
                
                essentialSpentToday = txns.filter { 
                    it.timestamp >= todayStart && it.categoryId in essentialCatIds && it.type == "Debit" 
                }.sumOf { it.amount }
                
                todaysAllowance = dailyBudget - essentialSpentToday"""

good_logic = """                val essentialBudget = budgets.filter { it.categoryId in essentialCatIds }.sumOf { it.amount }
                
                essentialSpentToday = txns.filter { 
                    it.timestamp >= todayStart && it.categoryId in essentialCatIds && it.type == "Debit" 
                }.sumOf { it.amount }

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
                    
                    // In fallback, Dashboard just shows (totalIncome - totalExpenses) / remainingDays
                    // It doesn't subtract today's expenses from that daily budget, because totalExpenses ALREADY includes today's expenses.
                    // Wait, let's look at Dashboard:
                    // `val totalExpenses = transactions.filter { it.type == "Debit" && it.timestamp >= startOfMonth }.sumOf { it.amount }`
                    // `remaining / remainingDaysIncludingToday`
                    todaysAllowance = remaining / remainingDaysIncludingToday
                }"""

content = content.replace(bad_logic, good_logic)

with open("app/src/main/java/com/example/financetracker/AllowanceWidget.kt", "w") as f:
    f.write(content)
