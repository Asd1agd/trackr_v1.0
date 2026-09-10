with open("app/src/main/java/com/example/financetracker/theme/Theme.kt", "r") as f:
    content = f.read()

# remove the appended block
content = content.split("import androidx.compose.animation.core.animateFloatAsState")[0]

imports = """
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
"""

package_line = "package com.example.financetracker.theme\n"
content = content.replace(package_line, package_line + imports)

modifier_code = """
fun Modifier.bounceClick(onClick: () -> Unit) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1f, label = "bounce")
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.clickable(
        interactionSource = interactionSource,
        indication = androidx.compose.foundation.LocalIndication.current,
        onClick = onClick
    )
}
"""

content += modifier_code

with open("app/src/main/java/com/example/financetracker/theme/Theme.kt", "w") as f:
    f.write(content)
