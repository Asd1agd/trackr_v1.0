with open("app/src/main/java/com/example/financetracker/theme/Theme.kt", "r") as f:
    content = f.read()

# Add primaryColor to FinanceTrackerTheme
content = content.replace(
    "fun FinanceTrackerTheme(\n  darkTheme: Boolean = isSystemInDarkTheme(),",
    "fun FinanceTrackerTheme(\n  darkTheme: Boolean = isSystemInDarkTheme(),\n  primaryColor: Color? = null,"
)

# Apply primary color to colorScheme
color_scheme_logic = """
  val finalColorScheme = if (primaryColor != null) {
      colorScheme.copy(primary = primaryColor)
  } else {
      colorScheme
  }

  MaterialTheme(
    colorScheme = finalColorScheme, 
"""
content = content.replace("  MaterialTheme(\n    colorScheme = colorScheme, \n", color_scheme_logic)

with open("app/src/main/java/com/example/financetracker/theme/Theme.kt", "w") as f:
    f.write(content)
