with open("app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "fun DashboardScreen(viewModel: FinanceViewModel, modifier: Modifier = Modifier, onNavigateToTransactions: () -> Unit = {}) {",
    "fun DashboardScreen(viewModel: FinanceViewModel, modifier: Modifier = Modifier, onNavigateToTransactions: () -> Unit = {}, onOpenProfile: () -> Unit = {}) {"
)

# Add missing imports
imports = """
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
"""
content = content.replace("import androidx.compose.foundation.Canvas", imports + "\nimport androidx.compose.foundation.Canvas")

with open("app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt", "w") as f:
    f.write(content)
