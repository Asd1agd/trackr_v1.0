# Add importLogs state flow and parsing method
sed -i '/val goals =/i \    private val _importLogs = MutableStateFlow<List<com.example.financetracker.data.ImportLog>>(emptyList())\n    val importLogs = _importLogs.asStateFlow()' app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt

sed -i '/init {/a \        viewModelScope.launch { repository.allImportLogs.collect { _importLogs.value = it } }' app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt

cat << 'INNER' >> app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt

    fun deleteImportLog(id: Int) {
        viewModelScope.launch {
            repository.deleteImportLog(id)
        }
    }

    suspend fun importYamlData(yamlText: String, filename: String): Boolean {
        try {
            val map = org.yaml.snakeyaml.Yaml().load<Map<String, Any>>(yamlText)
            
            // Validation
            if (map == null || (!map.containsKey("transactions") && !map.containsKey("goals") && !map.containsKey("budgets"))) {
                return false
            }

            val importId = repository.insertImportLog(filename, System.currentTimeMillis())
            
            val txns = map["transactions"] as? List<Map<String, Any>>
            txns?.forEach { tMap ->
                val amount = (tMap["amount"] as? Number)?.toDouble() ?: 0.0
                val type = (tMap["type"] as? String) ?: "Debit"
                val note = (tMap["note"] as? String) ?: ""
                val catName = (tMap["category"] as? String) ?: "Other"
                val ts = (tMap["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                
                var cat = categories.value.find { it.name.equals(catName, ignoreCase = true) }
                if (cat == null) {
                    addCategory(catName)
                    kotlinx.coroutines.delay(100) // wait for DB insert
                    cat = categories.value.find { it.name.equals(catName, ignoreCase = true) }
                }
                
                val finalCategoryId = cat?.id
                val isSubscription = note.lowercase().contains("subscription") || note.lowercase().contains("netflix") || note.lowercase().contains("spotify")
                val txn = com.example.financetracker.data.Transaction(
                    amount = amount,
                    type = type,
                    timestamp = ts,
                    categoryId = finalCategoryId,
                    note = note,
                    isSubscription = isSubscription,
                    importId = importId
                )
                repository.insertTransaction(txn)
            }
            
            val gls = map["goals"] as? List<Map<String, Any>>
            gls?.forEach { gMap ->
                val name = (gMap["name"] as? String) ?: "Unnamed"
                val targetAmt = (gMap["targetAmount"] as? Number)?.toDouble() ?: 0.0
                val savedAmt = (gMap["savedAmount"] as? Number)?.toDouble() ?: 0.0
                val targetDate = (gMap["targetDate"] as? Number)?.toLong() ?: 0L
                repository.addGoal(com.example.financetracker.data.Goal(name = name, targetAmount = targetAmt, targetDate = targetDate, currentAmount = savedAmt, importId = importId))
            }
            
            val bdgts = map["budgets"] as? List<Map<String, Any>>
            bdgts?.forEach { bMap ->
                val catName = (bMap["category"] as? String) ?: "Other"
                val amount = (bMap["amount"] as? Number)?.toDouble() ?: 0.0
                val month = (bMap["month"] as? Number)?.toInt() ?: java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
                val year = (bMap["year"] as? Number)?.toInt() ?: java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                
                var cat = categories.value.find { it.name.equals(catName, ignoreCase = true) }
                if (cat == null) {
                    addCategory(catName)
                    kotlinx.coroutines.delay(100)
                    cat = categories.value.find { it.name.equals(catName, ignoreCase = true) }
                }
                cat?.let { c ->
                    repository.saveBudget(com.example.financetracker.data.Budget(categoryId = c.id, amount = amount, month = month, year = year, importId = importId))
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
INNER

# Move the closing brace of FinanceViewModel to the very end
sed -i '/class FinanceViewModelFactory/i }' app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt
# We need to remove the previous closing brace of FinanceViewModel, which was just before class FinanceViewModelFactory.
# Wait, let's just append inside the class properly using replace_file_content instead of this messy bash.
