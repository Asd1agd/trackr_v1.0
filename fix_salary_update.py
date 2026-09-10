with open("app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt", "r") as f:
    content = f.read()

bad_salary = """                    val salaryExists = txns.any { it.amount == salaryAmount && it.type == "Credit" && it.note == "Monthly Salary" }

                    if (!salaryExists) {
                        repository.processNewTransaction(
                            amount = salaryAmount,
                            type = "Credit",
                            note = "Monthly Salary",
                            timestamp = firstDayTs,
                            manualCategoryId = salaryCatId
                        )
                    }"""

good_salary = """                    val existingSalaryTxn = txns.find { it.type == "Credit" && it.note == "Monthly Salary" }

                    if (existingSalaryTxn != null) {
                        if (existingSalaryTxn.amount != salaryAmount) {
                            repository.updateTransaction(existingSalaryTxn.copy(amount = salaryAmount, categoryId = salaryCatId))
                        }
                    } else {
                        repository.processNewTransaction(
                            amount = salaryAmount,
                            type = "Credit",
                            note = "Monthly Salary",
                            timestamp = firstDayTs,
                            manualCategoryId = salaryCatId
                        )
                    }"""

content = content.replace(bad_salary, good_salary)

with open("app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt", "w") as f:
    f.write(content)
