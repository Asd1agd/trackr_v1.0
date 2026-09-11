sed -i 's/val dueDate: Long? = null/val dueDate: Long? = null,\n    val importId: Int? = null/g' app/src/main/java/com/example/financetracker/data/Transaction.kt
sed -i 's/val targetDate: Long/val targetDate: Long,\n    val importId: Int? = null/g' app/src/main/java/com/example/financetracker/data/Goal.kt
sed -i 's/val year: Int/val year: Int,\n    val importId: Int? = null/g' app/src/main/java/com/example/financetracker/data/Budget.kt
