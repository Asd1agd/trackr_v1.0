with open("app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt", "r") as f:
    content = f.read()

bad_func = """
    private suspend fun updateWidget() {
        try {
            AllowanceWidget().updateAll(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
"""
content = content.replace(bad_func, "")

# insert it inside the class
good_func = """
    private suspend fun updateWidget() {
        try {
            AllowanceWidget().updateAll(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
"""
content = content.replace("val allCategories = categoryDao.getAllCategories()", "val allCategories = categoryDao.getAllCategories()\n" + good_func)

with open("app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt", "w") as f:
    f.write(content)
