with open("app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt", "r") as f:
    content = f.read()

# Extract the block
start_idx = content.find("        // Check and inject monthly salary")
end_idx = content.find("    val transactions:", start_idx)

extracted_block = content[start_idx:end_idx]

# We need to wrap it in a function
new_func = "    fun checkAndInjectMonthlySalary() {\n" + extracted_block + "    }\n\n"

# Leave the call in init block
content = content[:start_idx] + "        checkAndInjectMonthlySalary()\n" + content[end_idx:]
content = content.replace("    val transactions:", new_func + "    val transactions:")

with open("app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt", "w") as f:
    f.write(content)
