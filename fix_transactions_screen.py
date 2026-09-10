with open('app/src/main/java/com/example/financetracker/ui/TransactionsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'val transactions by viewModel.transactions.collectAsState()',
    'val transactions by viewModel.transactions.collectAsState()\n    val categoryFilter by viewModel.selectedCategoryFilter.collectAsState()'
)

list_code = """
        val displayTxns = if (categoryFilter != null) {
            transactions.filter { it.categoryId == categoryFilter }
        } else transactions
        
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("All Transactions" + if (categoryFilter != null) " (Filtered)" else "", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            if (categoryFilter != null) {
                TextButton(onClick = { viewModel.setCategoryFilter(null) }) { Text("Clear Filter") }
            }
        }
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(displayTxns) { txn ->
"""

content = content.replace(
    'Text("All Transactions", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))\n        \n        LazyColumn(modifier = Modifier.weight(1f)) {\n            items(transactions) { txn ->',
    list_code
)

with open('app/src/main/java/com/example/financetracker/ui/TransactionsScreen.kt', 'w') as f:
    f.write(content)
