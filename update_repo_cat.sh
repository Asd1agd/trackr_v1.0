# Remove the old addCategory method
sed -i '/suspend fun addCategory(name: String)/,+2d' app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt

# Insert the new one
sed -i '/suspend fun updateTransaction/i \    suspend fun addCategory(name: String): com.example.financetracker.data.Category {\n        val existing = categoryDao.getCategoriesSync().find { it.name.equals(name, ignoreCase = true) }\n        if (existing != null) return existing\n        val newCat = com.example.financetracker.data.Category(name = name, keywords = "")\n        val id = categoryDao.insert(newCat).toInt()\n        return newCat.copy(id = id)\n    }\n' app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt
