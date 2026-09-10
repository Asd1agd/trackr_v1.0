with open("app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt", "r") as f:
    content = f.read()

bad_call = """                        repository.processNewTransaction(com.example.financetracker.data.Transaction(
                            amount = salaryAmount,
                            type = "Credit",
                            note = "Monthly Salary",
                            timestamp = firstDayTs,
                            categoryId = salaryCatId
                        ))"""

good_call = """                        repository.processNewTransaction(
                            amount = salaryAmount,
                            type = "Credit",
                            note = "Monthly Salary",
                            timestamp = firstDayTs,
                            manualCategoryId = salaryCatId
                        )"""

content = content.replace(bad_call, good_call)

with open("app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt", "w") as f:
    f.write(content)
