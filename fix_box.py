with open("app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt", "r") as f:
    content = f.read()

# Let's remove the AnimatedVisibility at the end entirely
content = content.split("androidx.compose.animation.AnimatedVisibility(")[0]

# Now, we need to find the end of the Column that we wrapped in a Box.
# It's better to just put the AnimatedVisibility inside the Box right after the Column.
# Actually, the entire DashboardScreen is basically a Column. We wrapped it in a Box.
# So the end of that Column is right before `if (showEssentialCatsDialog) {`
# Let's inject AnimatedVisibility and closing brace for Box right before that if statement.

injection = """
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
    } // close Box
"""

content = content.replace("if (showEssentialCatsDialog) {", injection + "\n    if (showEssentialCatsDialog) {")

# Append closing brace for the function if we removed it
if not content.strip().endswith("}"):
    content += "\n}"

with open("app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt", "w") as f:
    f.write(content)
