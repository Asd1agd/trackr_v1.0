with open('app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt', 'r') as f:
    content = f.read()

alerts_code = """
        // Budget Alerts
        val budgetAlerts = mutableListOf<String>()
        val catSpentMap = transactions.filter { it.timestamp >= monthStart && it.type == "Debit" }.groupBy { it.categoryId }.mapValues { it.value.sumOf { t -> t.amount } }
        
        budgets.filter { it.month == currentMonth && it.year == currentYear }.forEach { budget ->
            val spent = catSpentMap[budget.categoryId] ?: 0.0
            val cName = categories.find { it.id == budget.categoryId }?.name ?: "Unknown"
            if (spent > budget.amount) {
                budgetAlerts.add("$cName: Over budget by ₹${"%.2f".format(spent - budget.amount)}!")
            } else if (spent > budget.amount * 0.9) {
                budgetAlerts.add("$cName: Approaching limit (₹${"%.2f".format(spent)} / ₹${"%.2f".format(budget.amount)})")
            }
        }
        
        if (budgetAlerts.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚠️ Budget Alerts", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    Spacer(modifier = Modifier.height(4.dp))
                    budgetAlerts.forEach { alert ->
                        Text(alert, color = Color(0xFFE65100), fontSize = 14.sp)
                    }
                }
            }
        }
"""

content = content.replace('// Summary Card', alerts_code + '\n        // Summary Card')

with open('app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt', 'w') as f:
    f.write(content)

