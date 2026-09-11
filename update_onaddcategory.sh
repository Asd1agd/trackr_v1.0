sed -i 's/viewModel.addCategory(newCategory)/viewModel.addCategory(newCategory) { added -> if (!added) android.widget.Toast.makeText(context, "Category already exists", android.widget.Toast.LENGTH_SHORT).show() }/g' app/src/main/java/com/example/financetracker/ui/MainScreen.kt

sed -i 's/viewModel.addCategory(newCategory)/viewModel.addCategory(newCategory) { added -> if (!added) android.widget.Toast.makeText(context, "Category already exists", android.widget.Toast.LENGTH_SHORT).show() }/g' app/src/main/java/com/example/financetracker/ui/TransactionsScreen.kt
