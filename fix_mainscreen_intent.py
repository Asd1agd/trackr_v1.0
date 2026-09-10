with open("app/src/main/java/com/example/financetracker/ui/MainScreen.kt", "r") as f:
    content = f.read()

# Add parameter
content = content.replace("fun MainScreen(viewModel: FinanceViewModel) {", "fun MainScreen(viewModel: FinanceViewModel, startWithAddDialog: Boolean = false) {")

# Init showAddDialog
content = content.replace("var showAddDialog by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }", "var showAddDialog by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(startWithAddDialog) }")

with open("app/src/main/java/com/example/financetracker/ui/MainScreen.kt", "w") as f:
    f.write(content)
