with open("app/src/main/java/com/example/financetracker/MainActivity.kt", "r") as f:
    content = f.read()

# Read preferences
prefs_logic = """
        val prefs = getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val openAddDialog = intent.getBooleanExtra("openAddDialog", false)
        
        setContent {
            var themeMode by remember { mutableStateOf(prefs.getString("theme_mode", "System Default") ?: "System Default") }
            var themeColorInt by remember { mutableStateOf(prefs.getInt("theme_color", 0)) }
            
            // Listen to changes
            DisposableEffect(Unit) {
                val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                    if (key == "theme_mode") {
                        themeMode = sharedPreferences.getString("theme_mode", "System Default") ?: "System Default"
                    } else if (key == "theme_color") {
                        themeColorInt = sharedPreferences.getInt("theme_color", 0)
                    }
                }
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
            }
            
            val isDark = when (themeMode) {
                "Dark Mode" -> true
                "Light Mode" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            
            val primaryColor = if (themeColorInt != 0) Color(themeColorInt) else null

            FinanceTrackerTheme(darkTheme = isDark, primaryColor = primaryColor) {
"""

content = content.replace("        val openAddDialog = intent.getBooleanExtra(\"openAddDialog\", false)\n        setContent {\n            FinanceTrackerTheme {", prefs_logic)
content = content.replace("import androidx.compose.runtime.LaunchedEffect", "import androidx.compose.runtime.LaunchedEffect\nimport androidx.compose.runtime.DisposableEffect")

with open("app/src/main/java/com/example/financetracker/MainActivity.kt", "w") as f:
    f.write(content)
