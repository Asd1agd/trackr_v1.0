import re

with open('app/src/main/java/com/example/financetracker/ui/TransactionsScreen.kt', 'r') as f:
    content = f.read()

# Replace basic Card with 3D rounded card
content = content.replace(
    'Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { onClick() })',
    'Card(\n        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { onClick() },\n        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),\n        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)\n    )'
)

# Add extra imports if needed
if 'import androidx.compose.foundation.shape.RoundedCornerShape' not in content:
    content = content.replace('import androidx.compose.ui.unit.sp', 'import androidx.compose.ui.unit.sp\nimport androidx.compose.foundation.shape.RoundedCornerShape')

with open('app/src/main/java/com/example/financetracker/ui/TransactionsScreen.kt', 'w') as f:
    f.write(content)
