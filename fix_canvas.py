with open("app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt", "r") as f:
    content = f.read()

old_canvas = """                Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                    val w = size.width
                    val h = size.height
                    val barH = h / 2f - 4.dp.toPx()
                    drawRect(color = Color(0xFFE0E0E0), topLeft = Offset(0f, 0f), size = Size(w, barH))
                    drawRect(color = Color(0xFF4CAF50), topLeft = Offset(0f, 0f), size = Size(w * inFraction, barH))
                    
                    drawRect(color = Color(0xFFE0E0E0), topLeft = Offset(0f, h / 2f + 2.dp.toPx()), size = Size(w, barH))
                    drawRect(color = Color(0xFFF44336), topLeft = Offset(0f, h / 2f + 2.dp.toPx()), size = Size(w * outFraction, barH))
                }"""

new_canvas = """                Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                    val w = size.width
                    val h = size.height
                    val barH = h / 2f - 4.dp.toPx()
                    val radius = androidx.compose.ui.geometry.CornerRadius(barH / 2, barH / 2)
                    
                    drawRoundRect(color = Color(0xFFE0E0E0), topLeft = Offset(0f, 0f), size = Size(w, barH), cornerRadius = radius)
                    if (inFraction > 0f) drawRoundRect(color = Color(0xFF4CAF50), topLeft = Offset(0f, 0f), size = Size(w * inFraction, barH), cornerRadius = radius)
                    
                    val bottomY = h / 2f + 2.dp.toPx()
                    drawRoundRect(color = Color(0xFFE0E0E0), topLeft = Offset(0f, bottomY), size = Size(w, barH), cornerRadius = radius)
                    if (outFraction > 0f) drawRoundRect(color = Color(0xFFF44336), topLeft = Offset(0f, bottomY), size = Size(w * outFraction, barH), cornerRadius = radius)
                }"""

content = content.replace(old_canvas, new_canvas)

with open("app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt", "w") as f:
    f.write(content)
