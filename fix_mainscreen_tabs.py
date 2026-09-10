with open('app/src/main/java/com/example/financetracker/ui/MainScreen.kt', 'r') as f:
    content = f.read()

import re
# Add icon import for Star or Flag
content = content.replace('import androidx.compose.material.icons.filled.List', 'import androidx.compose.material.icons.filled.List\nimport androidx.compose.material.icons.filled.Star')

nav_bar_items = """                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Transactions") },
                    label = { Text("Transactions") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Goals") },
                    label = { Text("Goals") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )"""

content = content.replace(
    '                NavigationBarItem(\n                    icon = { Icon(Icons.Default.List, contentDescription = "Transactions") },\n                    label = { Text("Transactions") },\n                    selected = selectedTab == 1,\n                    onClick = { selectedTab = 1 }\n                )',
    nav_bar_items
)

content = content.replace(
    '        if (selectedTab == 0) {\n            DashboardScreen(viewModel, modifier, onNavigateToTransactions = { selectedTab = 1 })\n        } else {\n            TransactionsScreen(viewModel, modifier)\n        }',
    '        if (selectedTab == 0) {\n            DashboardScreen(viewModel, modifier, onNavigateToTransactions = { selectedTab = 1 })\n        } else if (selectedTab == 1) {\n            TransactionsScreen(viewModel, modifier)\n        } else {\n            GoalsScreen(viewModel, modifier)\n        }'
)

with open('app/src/main/java/com/example/financetracker/ui/MainScreen.kt', 'w') as f:
    f.write(content)
