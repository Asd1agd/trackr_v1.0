with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace('import kotlinx.coroutines.flow.asStateFlow', '')
content = content.replace('import kotlinx.coroutines.flow.stateIn', 'import kotlinx.coroutines.flow.stateIn\nimport kotlinx.coroutines.flow.asStateFlow')

with open('app/src/main/java/com/example/financetracker/ui/FinanceViewModel.kt', 'w') as f:
    f.write(content)
