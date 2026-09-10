with open("app/src/main/java/com/example/financetracker/MainActivity.kt", "r") as f:
    content = f.read()

pointer_logic = """                Surface(
                    modifier = Modifier.fillMaxSize()
                        .pointerInput(isHapticsEnabled) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                                    val change = event.changes.firstOrNull()
                                    if (change != null) {
                                        if (change.changedToDownIgnoreConsumed()) {
                                            view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                                            if (isHapticsEnabled) {
                                                view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                                            }
                                            lastY = change.position.y
                                        }
                                    }
                                }
                            }
                        },"""

content = content.replace("                Surface(\n                    modifier = Modifier.fillMaxSize()\n                        .pointerInput(Unit) {\n                            awaitPointerEventScope {\n                                while (true) {\n                                    val event = awaitPointerEvent(PointerEventPass.Initial)\n                                    val change = event.changes.firstOrNull()\n                                    if (change != null) {\n                                        if (change.changedToDownIgnoreConsumed()) {\n                                            view.playSoundEffect(android.view.SoundEffectConstants.CLICK)\n                                            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)\n                                            lastY = change.position.y\n                                        }\n                                    }\n                                }\n                            }\n                        },", pointer_logic)

with open("app/src/main/java/com/example/financetracker/MainActivity.kt", "w") as f:
    f.write(content)
