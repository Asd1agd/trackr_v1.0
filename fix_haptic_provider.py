with open("app/src/main/java/com/example/financetracker/theme/Theme.kt", "r") as f:
    content = f.read()

# Add LocalHapticEnabled
content = content.replace("val AppShapes =", "val LocalHapticEnabled = androidx.compose.runtime.staticCompositionLocalOf { true }\n\nval AppShapes =")

# Update bounceClick to check LocalHapticEnabled
bounce_logic = """fun Modifier.bounceClick(onClick: () -> Unit) = composed {
    val hapticEnabled = LocalHapticEnabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed && hapticEnabled) 0.92f else 1f, label = "bounce")
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.clickable(
        interactionSource = interactionSource,
        indication = androidx.compose.foundation.LocalIndication.current,
        onClick = onClick
    )
}"""

content = content.replace(content[content.find("fun Modifier.bounceClick"):], bounce_logic)

with open("app/src/main/java/com/example/financetracker/theme/Theme.kt", "w") as f:
    f.write(content)
