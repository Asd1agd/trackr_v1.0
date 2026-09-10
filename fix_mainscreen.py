with open("app/src/main/java/com/example/financetracker/ui/MainScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    if "var showAddDialog" in line:
        new_lines.append(line)
        new_lines.append("    var showProfileSettings by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }\n")
        new_lines.append("    if (showProfileSettings) {\n        ProfileSettingsScreen(onBack = { showProfileSettings = false })\n        return\n    }\n")
    elif "DashboardScreen(viewModel, modifier," in line:
        new_lines.append(line.replace(")", ", onOpenProfile = { showProfileSettings = true })"))
    else:
        new_lines.append(line)

with open("app/src/main/java/com/example/financetracker/ui/MainScreen.kt", "w") as f:
    f.writelines(new_lines)
