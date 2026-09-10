content = """package com.example.financetracker.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.data.Transaction
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: FinanceViewModel, modifier: Modifier = Modifier, onNavigateToTransactions: () -> Unit = {}) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val budgets by viewModel.budgets.collectAsState()
    
    var currentFilter by remember { mutableStateOf("Daily") }
    var pieChartMode by remember { mutableStateOf("Today") }

    var showDateRangePicker by remember { mutableStateOf(false) }
    var customStartDate by remember { mutableStateOf<Long?>(null) }
    var customEndDate by remember { mutableStateOf<Long?>(null) }

    val now = System.currentTimeMillis()
    val filteredTxns = when (currentFilter) {
        "Daily" -> transactions.filter { now - it.timestamp < 86400000L }
        "Weekly" -> transactions.filter { now - it.timestamp < 86400000L * 7 }
        else -> transactions.filter { now - it.timestamp < 86400000L * 30 }
    }

    val totalInflow = filteredTxns.filter { it.type == "Credit" }.sumOf { it.amount }
    val totalOutflow = filteredTxns.filter { it.type == "Debit" }.sumOf { it.amount }
    
    val days = when (currentFilter) {
        "Daily" -> 1
        "Weekly" -> 7
        else -> 30
    }
    val avgSpend = totalOutflow / days
    
    // Auto-Compensating Budget logic
    val cal = Calendar.getInstance()
    val currentMonth = cal.get(Calendar.MONTH)
    val currentYear = cal.get(Calendar.YEAR)
    val essentialCatIds = categories.filter { it.isEssential }.map { it.id }
    val essentialBudget = budgets.filter { it.month == currentMonth && it.year == currentYear && it.categoryId in essentialCatIds }.sumOf { it.amount }
    
    val monthStart = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    
    val essentialSpent = transactions.filter { it.timestamp >= monthStart && it.categoryId in essentialCatIds && it.type == "Debit" }.sumOf { it.amount }
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val currentDay = cal.get(Calendar.DAY_OF_MONTH)
    val remainingDays = (maxDays - currentDay + 1).coerceAtLeast(1)
    val dailyAllowance = ((essentialBudget - essentialSpent) / remainingDays)

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        
        // Essential Daily Budget Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Everyday Essentials Allowance", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    text = if (dailyAllowance >= 0) "₹${"%.2f".format(dailyAllowance)} / day" else "₹${"%.2f".format(dailyAllowance)} (Overspent!)",
                    fontSize = 28.sp, 
                    fontWeight = FontWeight.ExtraBold, 
                    color = if (dailyAllowance >= 0) MaterialTheme.colorScheme.primary else Color.Red
                )
                Text("Remaining Month: $remainingDays days", fontSize = 12.sp)
            }
        }

        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Dashboard", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Daily", "Weekly", "Monthly").forEach { filter ->
                        FilterChip(
                            selected = currentFilter == filter,
                            onClick = { currentFilter = filter },
                            label = { Text(filter) }
                        )
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Inflow", color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold)
                        Text("₹${"%.2f".format(totalInflow)}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Outflow", color = Color(0xFFF44336), fontWeight = FontWeight.SemiBold)
                        Text("₹${"%.2f".format(totalOutflow)}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                // Income vs Expense Chart
                val maxVal = maxOf(totalInflow, totalOutflow).coerceAtLeast(1.0)
                val inFraction = (totalInflow / maxVal).toFloat()
                val outFraction = (totalOutflow / maxVal).toFloat()
                
                Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                    val w = size.width
                    val h = size.height
                    val barH = h / 2f - 4.dp.toPx()
                    drawRect(color = Color(0xFFE0E0E0), topLeft = Offset(0f, 0f), size = Size(w, barH))
                    drawRect(color = Color(0xFF4CAF50), topLeft = Offset(0f, 0f), size = Size(w * inFraction, barH))
                    
                    drawRect(color = Color(0xFFE0E0E0), topLeft = Offset(0f, h/2f + 4.dp.toPx()), size = Size(w, barH))
                    drawRect(color = Color(0xFFF44336), topLeft = Offset(0f, h/2f + 4.dp.toPx()), size = Size(w * outFraction, barH))
                }
                Text("Avg Spend/Day: ₹${"%.2f".format(avgSpend)}", fontWeight = FontWeight.Medium, modifier = Modifier.padding(top=8.dp))
            }
        }
        
        // Category Pie Chart Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Category Spending", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Row(modifier = Modifier.padding(vertical = 12.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Today", "Daily", "Monthly", "Daily Avg", "Custom Avg").forEach { mode ->
                        FilterChip(
                            selected = pieChartMode == mode, 
                            onClick = { 
                                pieChartMode = mode 
                                if (mode == "Custom Avg") {
                                    showDateRangePicker = true
                                }
                            }, 
                            label = { Text(mode) }
                        )
                    }
                }
                
                val todayStart = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                val daysElapsedThisMonth = ((now - monthStart) / 86400000L).coerceAtLeast(1).toInt()
                
                var chartTxns = emptyList<Transaction>()
                var divisor = 1

                when (pieChartMode) {
                    "Today" -> {
                        chartTxns = transactions.filter { it.timestamp >= todayStart && it.type == "Debit" }
                    }
                    "Daily" -> {
                        chartTxns = transactions.filter { now - it.timestamp < 86400000L && it.type == "Debit" }
                    }
                    "Monthly" -> {
                        chartTxns = transactions.filter { it.timestamp >= monthStart && it.type == "Debit" }
                    }
                    "Daily Avg" -> {
                        chartTxns = transactions.filter { it.timestamp >= monthStart && it.type == "Debit" }
                        divisor = daysElapsedThisMonth
                    }
                    "Custom Avg" -> {
                        if (customStartDate != null && customEndDate != null) {
                            val realEnd = customEndDate!! + 86400000L // inclusive
                            chartTxns = transactions.filter { it.timestamp in customStartDate!!..realEnd && it.type == "Debit" }
                            val customDays = ((realEnd - customStartDate!!) / 86400000L).coerceAtLeast(1).toInt()
                            divisor = customDays
                        }
                    }
                }
                
                // Group by category ID
                val grouped = chartTxns.groupBy { it.categoryId }.mapValues { it.value.sumOf { t -> t.amount } / divisor }
                val totalSum = grouped.values.sum().coerceAtLeast(1.0)
                
                val categoryMap = grouped.mapKeys { entry -> categories.find { it.id == entry.key }?.name ?: "Other" }
                val catIdMap = grouped.mapKeys { entry -> entry.key }
                    
                if (categoryMap.isEmpty()) {
                    Text("No expenses to show.", modifier = Modifier.padding(32.dp))
                } else {
                    val colors = listOf(Color(0xFFE57373), Color(0xFF64B5F6), Color(0xFF81C784), Color(0xFFFFD54F), Color(0xFFBA68C8), Color(0xFF4DB6AC), Color(0xFFFF8A65))
                    PieChart(data = categoryMap, colors = colors, modifier = Modifier.padding(16.dp))
                    
                    Column(modifier = Modifier.fillMaxWidth()) {
                        catIdMap.entries.sortedByDescending { it.value }.forEachIndexed { index, entry ->
                            val cName = categories.find { it.id == entry.key }?.name ?: "Other"
                            val pct = (entry.value / totalSum) * 100
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable {
                                viewModel.setCategoryFilter(entry.key)
                                onNavigateToTransactions()
                            }) {
                                Surface(modifier = Modifier.size(16.dp), color = colors[index % colors.size], shape = RoundedCornerShape(4.dp)) {}
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(cName, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                                Text("${"%.1f".format(pct)}%  (₹${"%.2f".format(entry.value)})", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showDateRangePicker) {
        val dateRangePickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    customStartDate = dateRangePickerState.selectedStartDateMillis
                    customEndDate = dateRangePickerState.selectedEndDateMillis
                    showDateRangePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
"""
with open('app/src/main/java/com/example/financetracker/ui/DashboardScreen.kt', 'w') as f:
    f.write(content)
