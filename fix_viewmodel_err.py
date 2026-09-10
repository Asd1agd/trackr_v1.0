with open("app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt", "r") as f:
    content = f.read()

bad_init = """    init {

        checkAndInjectMonthlySalary()
    fun checkAndInjectMonthlySalary() {"""

good_init = """    init {
        checkAndInjectMonthlySalary()
    }

    fun checkAndInjectMonthlySalary() {"""

content = content.replace(bad_init, good_init)

with open("app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt", "w") as f:
    f.write(content)
