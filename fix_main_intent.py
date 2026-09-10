with open("app/src/main/java/com/example/financetracker/MainActivity.kt", "r") as f:
    content = f.read()

# Modify onCreate to check intent
intent_check = "val openAddDialog = intent.getBooleanExtra(\"openAddDialog\", false)\n"
content = content.replace("setContent {", intent_check + "        setContent {")

# Modify showSplash initialization
content = content.replace("var showSplash by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(true) }", "var showSplash by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(!openAddDialog) }")

# Modify MainScreen call
content = content.replace("MainScreen(viewModel = viewModel)", "MainScreen(viewModel = viewModel, startWithAddDialog = openAddDialog)")

with open("app/src/main/java/com/example/financetracker/MainActivity.kt", "w") as f:
    f.write(content)
