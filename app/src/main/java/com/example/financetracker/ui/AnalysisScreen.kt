package com.example.financetracker.ui
import com.example.financetracker.theme.bounceClick

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.data.Transaction
import com.example.financetracker.data.Category
import java.util.Calendar
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput

data class BarSegment(val value: Float, val color: Color, val label: String)
data class BarData(val label: String, val segments: List<BarSegment>)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(viewModel: FinanceViewModel, modifier: Modifier = Modifier) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    
    var timeFrame by remember { mutableStateOf("Week") } // Week, Month, Year
    var selectedGraph by remember { mutableStateOf("Category") } // Category, IncomeOutgo, SalarySpendSavings
    val view = androidx.compose.ui.platform.LocalView.current
    var offsetCycle by remember { mutableStateOf(0) }
    
    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Analysis", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(start = 16.dp, top = 32.dp, bottom = 16.dp))
        
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Week", "Month", "Year").forEach { tf ->
                FilterChip(selected = timeFrame == tf, onClick = { timeFrame = tf; offsetCycle = 0 }, label = { Text(tf, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) })
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Category", "Income vs Outgo", "Salary vs Spend").forEach { graph ->
                FilterChip(selected = selectedGraph == graph, onClick = { selectedGraph = graph; offsetCycle = 0 }, label = { Text(graph, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) })
            }
        }
        
        val colors = listOf(Color(0xFFE57373), Color(0xFF64B5F6), Color(0xFF81C784), Color(0xFFFFD54F), Color(0xFFBA68C8), Color(0xFF4DB6AC), Color(0xFFFF8A65))
        val cal = Calendar.getInstance()
        
        var swipeThreshold by remember { mutableStateOf(0f) }
        val chartModifier = Modifier.pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    if (swipeThreshold > 50) {
                        offsetCycle -= 1
                        view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK)
                    } else if (swipeThreshold < -50) {
                        offsetCycle += 1
                        view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK)
                    }
                    swipeThreshold = 0f
                }
            ) { change, dragAmount ->
                change.consume()
                swipeThreshold += dragAmount
            }
        }

        Box(modifier = chartModifier) {
            if (selectedGraph == "Category") {
                val catBarData = generateCategoryBarData(transactions, categories, timeFrame, offsetCycle, colors, cal)
                ChartCard("Category Spending", catBarData)
            } else if (selectedGraph == "Income vs Outgo") {
                val ioBarData = generateIncomeOutgoBarData(transactions, timeFrame, offsetCycle, cal)
                ChartCard("Income vs Outgo", ioBarData)
            } else {
                val sssBarData = generateSalarySpendSavingsBarData(transactions, categories, timeFrame, offsetCycle, cal)
                ChartCard("Salary vs Spend vs Savings", sssBarData)
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChartCard(title: String, data: List<BarData>) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(16.dp))
            StackedBarChart(data = data, modifier = Modifier.fillMaxWidth().height(250.dp))
            
            Spacer(modifier = Modifier.height(16.dp))
            // Legend
            val uniqueSegments = mutableMapOf<String, Color>()
            data.forEach { bar -> bar.segments.forEach { seg -> uniqueSegments[seg.label] = seg.color } }
            
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uniqueSegments.forEach { (label, color) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(label, fontSize = 12.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
fun StackedBarChart(data: List<BarData>, modifier: Modifier = Modifier) {
    if (data.isEmpty() || data.all { it.segments.isEmpty() || it.segments.sumOf { s -> s.value.toDouble() } == 0.0 }) {
        Text("No data", modifier = modifier.wrapContentSize(Alignment.Center))
        return
    }
    
    val maxTotal = data.maxOf { it.segments.sumOf { s -> s.value.toDouble() } }.toFloat().coerceAtLeast(1f)
    
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        val yAxisWidth = 40.dp.toPx()
        val chartW = w - yAxisWidth
        val chartH = h - 20.dp.toPx()
        
        val barWidth = (chartW / (data.size * 2f)).coerceAtMost(50.dp.toPx())
        val spacing = (chartW - (barWidth * data.size)) / (data.size + 1)
        
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 10.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
        }
        
        val yAxisTextPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 10.sp.toPx()
            textAlign = android.graphics.Paint.Align.RIGHT
        }
        
        // Draw Y-axis labels and grid lines
        val steps = 4
        for (i in 0..steps) {
            val yVal = maxTotal * (i.toFloat() / steps)
            val yPos = chartH - (i.toFloat() / steps) * chartH
            
            drawContext.canvas.nativeCanvas.drawText(
                if (yVal >= 1000) "${"%.1f".format(yVal / 1000)}k" else yVal.toInt().toString(),
                yAxisWidth - 8.dp.toPx(),
                yPos + 4.dp.toPx(),
                yAxisTextPaint
            )
            
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(yAxisWidth, yPos),
                end = Offset(w, yPos),
                strokeWidth = 1f
            )
        }
        
        for (i in data.indices) {
            val barData = data[i]
            val x = yAxisWidth + spacing + i * (barWidth + spacing) + barWidth / 2f
            
            var currentY = chartH
            
            for (segment in barData.segments) {
                if (segment.value <= 0f) continue
                val segmentHeight = (segment.value / maxTotal) * chartH
                val topY = currentY - segmentHeight
                
                drawRect(
                    color = segment.color,
                    topLeft = Offset(x - barWidth / 2f, topY),
                    size = Size(barWidth, segmentHeight)
                )
                currentY = topY
            }
            
            drawContext.canvas.nativeCanvas.drawText(
                barData.label,
                x,
                h,
                textPaint
            )
        }
    }
}

fun generateCategoryBarData(transactions: List<Transaction>, categories: List<Category>, timeFrame: String, offsetCycle: Int, colors: List<Color>, cal: Calendar): List<BarData> {
    return buildBarData(transactions, timeFrame, cal, offsetCycle) { txns ->
        val debits = txns.filter { it.type == "Debit" }
        val grouped = debits.groupBy { it.categoryId }.mapValues { it.value.sumOf { t -> t.amount }.toFloat() }
        val result = mutableListOf<BarSegment>()
        var colorIdx = 0
        grouped.forEach { (catId, amt) ->
            val catName = categories.find { it.id == catId }?.name ?: "Other"
            result.add(BarSegment(amt, colors[colorIdx % colors.size], catName))
            colorIdx++
        }
        result
    }
}

fun generateIncomeOutgoBarData(transactions: List<Transaction>, timeFrame: String, offsetCycle: Int, cal: Calendar): List<BarData> {
    return buildBarData(transactions, timeFrame, cal, offsetCycle) { txns ->
        val income = txns.filter { it.type == "Credit" }.sumOf { it.amount }.toFloat()
        val outgo = txns.filter { it.type == "Debit" }.sumOf { it.amount }.toFloat()
        listOf(
            BarSegment(income, Color(0xFF4CAF50), "Income"),
            BarSegment(outgo, Color(0xFFF44336), "Outgo")
        )
    }
}

fun generateSalarySpendSavingsBarData(transactions: List<Transaction>, categories: List<Category>, timeFrame: String, offsetCycle: Int, cal: Calendar): List<BarData> {
    return buildBarData(transactions, timeFrame, cal, offsetCycle) { txns ->
        val salaryCat = categories.find { it.name.equals("Salary", ignoreCase = true) }
        val salary = txns.filter { it.type == "Credit" && it.categoryId == salaryCat?.id }.sumOf { it.amount }.toFloat()
        val spend = txns.filter { it.type == "Debit" }.sumOf { it.amount }.toFloat()
        val savings = (salary - spend).coerceAtLeast(0f)
        listOf(
            BarSegment(salary, Color(0xFF64B5F6), "Salary"),
            BarSegment(spend, Color(0xFFF44336), "Spend"),
            BarSegment(savings, Color(0xFF81C784), "Savings")
        )
    }
}

fun buildBarData(transactions: List<Transaction>, timeFrame: String, cal: Calendar, offsetCycle: Int, segmentBuilder: (List<Transaction>) -> List<BarSegment>): List<BarData> {
    val result = mutableListOf<BarData>()
    val today = System.currentTimeMillis()
    cal.timeInMillis = today
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    
    when (timeFrame) {
        "Week" -> {
            cal.firstDayOfWeek = Calendar.MONDAY
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            cal.add(Calendar.WEEK_OF_YEAR, offsetCycle)
            var start = cal.timeInMillis
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            for (i in 0..6) {
                val end = start + 86400000L
                val txns = transactions.filter { it.timestamp in start until end }
                result.add(BarData(days[i], segmentBuilder(txns)))
                start = end
            }
        }
        "Month" -> {
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.add(Calendar.MONTH, offsetCycle)
            val monthStart = cal.timeInMillis
            cal.add(Calendar.MONTH, 1)
            val monthEnd = cal.timeInMillis
            var current = monthStart
            var weekNum = 1
            while (current < monthEnd) {
                val next = (current + 86400000L * 7).coerceAtMost(monthEnd)
                val txns = transactions.filter { it.timestamp in current until next }
                result.add(BarData("W$weekNum", segmentBuilder(txns)))
                current = next
                weekNum++
            }
        }
        "Year" -> {
            cal.set(Calendar.MONTH, Calendar.JANUARY)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.add(Calendar.YEAR, offsetCycle)
            val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            for (i in 0..11) {
                val start = cal.timeInMillis
                cal.add(Calendar.MONTH, 1)
                val end = cal.timeInMillis
                val txns = transactions.filter { it.timestamp in start until end }
                result.add(BarData(months[i], segmentBuilder(txns)))
            }
        }
    }
    return result
}
