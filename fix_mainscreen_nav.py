with open('app/src/main/java/com/example/financetracker/ui/MainScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'DashboardScreen(viewModel, modifier)',
    'DashboardScreen(viewModel, modifier, onNavigateToTransactions = { selectedTab = 1 })'
)

with open('app/src/main/java/com/example/financetracker/ui/MainScreen.kt', 'w') as f:
    f.write(content)
