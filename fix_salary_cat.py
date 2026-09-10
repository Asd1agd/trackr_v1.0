with open("app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt", "r") as f:
    content = f.read()

bad_salary = """                    val txns = repository.getTransactionsBetween(firstDayTs, firstDayTs + 86400000L * 31).first()
                    val salaryExists = txns.any { it.amount == salaryAmount && it.type == "Credit" && it.note == "Monthly Salary" }

                    if (!salaryExists) {
                        repository.insertTransaction(com.example.financetracker.data.Transaction(
                            amount = salaryAmount,
                            type = "Credit",
                            note = "Monthly Salary",
                            timestamp = firstDayTs,
                            categoryId = 0
                        ))
                    }"""

good_salary = """                    repository.ensureSalaryCategoryExists()
                    val cats = repository.allCategories.first()
                    val salaryCat = cats.find { it.name.equals("Salary", ignoreCase = true) }
                    val salaryCatId = salaryCat?.id ?: 0

                    val txns = repository.getTransactionsBetween(firstDayTs, firstDayTs + 86400000L * 31).first()
                    val salaryExists = txns.any { it.amount == salaryAmount && it.type == "Credit" && it.note == "Monthly Salary" }

                    if (!salaryExists) {
                        repository.insertTransaction(com.example.financetracker.data.Transaction(
                            amount = salaryAmount,
                            type = "Credit",
                            note = "Monthly Salary",
                            timestamp = firstDayTs,
                            categoryId = salaryCatId
                        ))
                    }"""

content = content.replace(bad_salary, good_salary)

# We also need to remove the duplicate `repository.ensureSalaryCategoryExists()` at the bottom of the init block
content = content.replace("        viewModelScope.launch {\n            repository.ensureSalaryCategoryExists()\n        }\n", "")

with open("app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt", "w") as f:
    f.write(content)
