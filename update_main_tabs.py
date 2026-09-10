with open('app/src/main/java/com/example/financetracker/ui/MainScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'import androidx.compose.material.icons.filled.Star',
    'import androidx.compose.material.icons.filled.Star\nimport androidx.compose.material.icons.filled.AccountBalanceWallet'
)

nav_bar_items = """                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Goals") },
                    label = { Text("Goals") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Budgets") },
                    label = { Text("Budgets") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )"""

content = content.replace(
    '                NavigationBarItem(\n                    icon = { Icon(Icons.Default.Star, contentDescription = "Goals") },\n                    label = { Text("Goals") },\n                    selected = selectedTab == 2,\n                    onClick = { selectedTab = 2 }\n                )',
    nav_bar_items
)

content = content.replace(
    '        } else {\n            GoalsScreen(viewModel, modifier)\n        }',
    '        } else if (selectedTab == 2) {\n            GoalsScreen(viewModel, modifier)\n        } else {\n            BudgetsScreen(viewModel, modifier)\n        }'
)

with open('app/src/main/java/com/example/financetracker/ui/MainScreen.kt', 'w') as f:
    f.write(content)

