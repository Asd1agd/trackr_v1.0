with open("app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt", "r") as f:
    content = f.read()

# Replace the signature
content = content.replace(
    "fun DashboardScreen(viewModel: FinanceViewModel, modifier: Modifier = Modifier, onNavigateToTransactions: () -> Unit) {",
    "fun DashboardScreen(viewModel: FinanceViewModel, modifier: Modifier = Modifier, onNavigateToTransactions: () -> Unit, onOpenProfile: () -> Unit = {}) {"
)

# Replace Column and add scrollState logic
old_column = "Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {"
new_column = """
    val scrollState = androidx.compose.foundation.rememberScrollState()
    var isProfileVisible by remember { mutableStateOf(false) }

    LaunchedEffect(scrollState.isScrollInProgress) {
        if (scrollState.isScrollInProgress) {
            isProfileVisible = true
        } else {
            kotlinx.coroutines.delay(3000)
            isProfileVisible = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
"""
content = content.replace(old_column, new_column)

# Need to close the Box at the end of DashboardScreen
# The last brace of the file is likely the end of DashboardScreen.
lines = content.split('\n')
for i in range(len(lines)-1, -1, -1):
    if lines[i].strip() == "}":
        lines.insert(i, """
        androidx.compose.animation.AnimatedVisibility(
            visible = isProfileVisible,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut(),
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            IconButton(
                onClick = onOpenProfile,
                modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, androidx.compose.foundation.shape.CircleShape)
            ) {
                Icon(androidx.compose.material.icons.Icons.Default.Person, contentDescription = "Profile")
            }
        }
    }
""")
        lines.pop(i+1) # remove the original closing brace of the Column, we added two braces
        break

with open("app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt", "w") as f:
    f.write("\n".join(lines))
