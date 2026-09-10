with open("app/src/main/java/com/example/financetracker/ui/MainScreen.kt", "r") as f:
    content = f.read()

bad = "ProfileSettingsScreen(onBack = { showProfileSettings = false; viewModel.checkAndInjectMonthlySalary() })"
good = "ProfileSettingsScreen(onBack = { showProfileSettings = false }, onSave = { showProfileSettings = false; viewModel.checkAndInjectMonthlySalary() })"
content = content.replace(bad, good)

with open("app/src/main/java/com/example/financetracker/ui/MainScreen.kt", "w") as f:
    f.write(content)
