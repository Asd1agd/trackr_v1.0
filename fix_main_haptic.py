with open("app/src/main/java/com/example/financetracker/MainActivity.kt", "r") as f:
    content = f.read()

state_logic = """            var themeColorInt by remember { mutableStateOf(prefs.getInt("theme_color", 0)) }
            var isHapticsEnabled by remember { mutableStateOf(prefs.getBoolean("haptics_enabled", true)) }
            
            // Listen to changes
            DisposableEffect(Unit) {
                val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                    if (key == "theme_mode") {
                        themeMode = sharedPreferences.getString("theme_mode", "System Default") ?: "System Default"
                    } else if (key == "theme_color") {
                        themeColorInt = sharedPreferences.getInt("theme_color", 0)
                    } else if (key == "haptics_enabled") {
                        isHapticsEnabled = sharedPreferences.getBoolean("haptics_enabled", true)
                    }
                }"""

content = content.replace(
    """            var themeColorInt by remember { mutableStateOf(prefs.getInt("theme_color", 0)) }
            
            // Listen to changes
            DisposableEffect(Unit) {
                val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                    if (key == "theme_mode") {
                        themeMode = sharedPreferences.getString("theme_mode", "System Default") ?: "System Default"
                    } else if (key == "theme_color") {
                        themeColorInt = sharedPreferences.getInt("theme_color", 0)
                    }
                }""", state_logic)

provider_logic = """            FinanceTrackerTheme(darkTheme = isDark, primaryColor = primaryColor) {
                androidx.compose.runtime.CompositionLocalProvider(com.example.financetracker.theme.LocalHapticEnabled provides isHapticsEnabled) {"""

content = content.replace("            FinanceTrackerTheme(darkTheme = isDark, primaryColor = primaryColor) {", provider_logic)

content = content.replace("                }\n            }\n        }\n    }\n}", "                }\n            }\n        }\n        }\n    }\n}")

with open("app/src/main/java/com/example/financetracker/MainActivity.kt", "w") as f:
    f.write(content)
