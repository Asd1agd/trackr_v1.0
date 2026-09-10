with open("app/src/main/java/com/example/financetracker/ui/ProfileSettingsScreen.kt", "r") as f:
    content = f.read()

# Add hapticsEnabled state
content = content.replace("var themeMode by remember { mutableStateOf", "var hapticsEnabled by remember { mutableStateOf(prefs.getBoolean(\"haptics_enabled\", true)) }\n    var themeMode by remember { mutableStateOf")

# Add Haptics Switch to UI
haptics_ui = """            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Enable Haptics", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.weight(1f))
                Switch(checked = hapticsEnabled, onCheckedChange = { 
                    hapticsEnabled = it
                    prefs.edit().putBoolean("haptics_enabled", it).apply()
                })
            }"""

content = content.replace("Spacer(modifier = Modifier.height(32.dp))\n            Text(\"Appearance\"", haptics_ui + "\n            Spacer(modifier = Modifier.height(32.dp))\n            Text(\"Appearance\"")

with open("app/src/main/java/com/example/financetracker/ui/ProfileSettingsScreen.kt", "w") as f:
    f.write(content)
