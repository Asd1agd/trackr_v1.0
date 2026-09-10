package com.example.financetracker.ui

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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: FinanceViewModel, modifier: Modifier = Modifier, onNavigateToTransactions: () -> Unit = {}) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val budgets by viewModel.budgets.collectAsState()
    val selectedEssentialCategoryIds by viewModel.essentialCategoryIds.collectAsState(initial = emptySet())
    
    var currentFilter by remember { mutableStateOf("Daily") }
    var pieChartMode by remember { mutableStateOf("Today") }

    var showDateRangePicker by remember { mutableStateOf(false) }
    var showSingleDatePicker by remember { mutableStateOf(false) }
    var showEssentialCatsDialog by remember { mutableStateOf(false) }
    var customStartDate by remember { mutableStateOf<Long?>(null) }
    var customEndDate by remember { mutableStateOf<Long?>(null) }
    var customSingleDate by remember { mutableStateOf<Long?>(null) }

    val now = System.currentTimeMillis()
    
    val cal = Calendar.getInstance()
    val currentMonth = cal.get(Calendar.MONTH)
    val currentYear = cal.get(Calendar.YEAR)

    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val weekStart = Calendar.getInstance().apply {
        timeInMillis = todayStart
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }.timeInMillis
    
    val monthStart = Calendar.getInstance().apply {
        timeInMillis = todayStart
        set(Calendar.DAY_OF_MONTH, 1)
    }.timeInMillis

    val filteredTxns = when (currentFilter) {
        "Daily" -> transactions.filter { it.timestamp >= todayStart }
        "Weekly" -> transactions.filter { it.timestamp >= weekStart }
        else -> transactions.filter { it.timestamp >= monthStart }
    }

    val totalInflow = filteredTxns.filter { it.type == "Credit" }.sumOf { it.amount }
    val totalOutflow = filteredTxns.filter { it.type == "Debit" }.sumOf { it.amount }
    
    val days = when (currentFilter) {
        "Daily" -> 1
        "Weekly" -> {
            val d = ((todayStart - weekStart) / 86400000L).toInt() + 1
            if (d <= 0) 1 else d
        }
        else -> {
            val d = ((todayStart - monthStart) / 86400000L).toInt() + 1
            if (d <= 0) 1 else d
        }
    }
    val avgSpend = totalOutflow / days
    
    // Auto-Compensating Budget logic
    val essentialCatIds = if (selectedEssentialCategoryIds.isEmpty()) categories.filter { it.isEssential }.map { it.id } else selectedEssentialCategoryIds.toList()
    val essentialBudget = budgets.filter { it.month == currentMonth && it.year == currentYear && it.categoryId in essentialCatIds }.sumOf { it.amount }
    
    val essentialSpentTillYesterday = transactions.filter { 
        it.timestamp >= monthStart && it.timestamp < todayStart && it.categoryId in essentialCatIds && it.type == "Debit" 
    }.sumOf { it.amount }
    
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val currentDay = cal.get(Calendar.DAY_OF_MONTH)
    val remainingDaysIncludingToday = (maxDays - currentDay + 1).coerceAtLeast(1)
    
    val dailyBudget = (essentialBudget - essentialSpentTillYesterday) / remainingDaysIncludingToday
    
    val essentialSpentToday = transactions.filter { 
        it.timestamp >= todayStart && it.categoryId in essentialCatIds && it.type == "Debit" 
    }.sumOf { it.amount }
    
    val todaysAllowance = dailyBudget - essentialSpentToday

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        
        // Essential Daily Budget Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { showEssentialCatsDialog = true },
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Today's Allowance", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    text = if (todaysAllowance >= 0) "₹${"%.2f".format(todaysAllowance)}" else "₹${"%.2f".format(todaysAllowance)} (Overspent!)",
                    fontSize = 28.sp, 
                    fontWeight = FontWeight.ExtraBold, 
                    color = if (todaysAllowance >= 0) MaterialTheme.colorScheme.primary else Color.Red
                )
                Text("Based on monthly budget & total spend", fontSize = 12.sp)
            }
        }

        
        // Budget Alerts
        val budgetAlerts = mutableListOf<String>()
        val catSpentMap = transactions.filter { it.timestamp >= monthStart && it.type == "Debit" }.groupBy { it.categoryId }.mapValues { it.value.sumOf { t -> t.amount } }
        
        budgets.filter { it.month == currentMonth && it.year == currentYear }.forEach { budget ->
            val spent = catSpentMap[budget.categoryId] ?: 0.0
            val cName = categories.find { it.id == budget.categoryId }?.name ?: "Unknown"
            if (spent > budget.amount) {
                budgetAlerts.add("$cName: Over budget by ₹${"%.2f".format(spent - budget.amount)}!")
            } else if (spent > budget.amount * 0.9) {
                budgetAlerts.add("$cName: Approaching limit (₹${"%.2f".format(spent)} / ₹${"%.2f".format(budget.amount)})")
            }
        }
        
        if (budgetAlerts.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚠️ Budget Alerts", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    Spacer(modifier = Modifier.height(4.dp))
                    budgetAlerts.forEach { alert ->
                        Text(alert, color = Color(0xFFE65100), fontSize = 14.sp)
                    }
                }
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
            modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { showEssentialCatsDialog = true },
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Category Spending", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Row(modifier = Modifier.padding(vertical = 12.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val dateLabel = if (customSingleDate != null) SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(customSingleDate!!)) else "Select Date"
                    listOf("Today", dateLabel, "Monthly", "Daily Avg", "Custom Avg").forEach { mode ->
                        FilterChip(
                            selected = pieChartMode == mode || (mode == dateLabel && pieChartMode == "Select Date"), 
                            onClick = { 
                                if (mode == dateLabel) {
                                    showSingleDatePicker = true
                                    pieChartMode = "Select Date"
                                } else if (mode == "Custom Avg") {
                                    showDateRangePicker = true
                                    pieChartMode = mode
                                } else {
                                    pieChartMode = mode
                                }
                            }, 
                            label = { Text(mode) }
                        )
                    }
                }
                
                val daysElapsedThisMonth = ((now - monthStart) / 86400000L).coerceAtLeast(1).toInt()
                
                var chartTxns = emptyList<Transaction>()
                var divisor = 1

                when (pieChartMode) {
                    "Today" -> {
                        chartTxns = transactions.filter { it.timestamp >= todayStart && it.type == "Debit" }
                    }
                    "Select Date" -> {
                        if (customSingleDate != null) {
                            val endOfDay = customSingleDate!! + 86400000L
                            chartTxns = transactions.filter { it.timestamp in customSingleDate!! until endOfDay && it.type == "Debit" }
                        }
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
                    val sStart = dateRangePickerState.selectedStartDateMillis
                    if (sStart != null) {
                        val utcCal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply { timeInMillis = sStart }
                        val localCal = java.util.Calendar.getInstance().apply {
                            timeInMillis = System.currentTimeMillis()
                            set(java.util.Calendar.YEAR, utcCal.get(java.util.Calendar.YEAR))
                            set(java.util.Calendar.MONTH, utcCal.get(java.util.Calendar.MONTH))
                            set(java.util.Calendar.DAY_OF_MONTH, utcCal.get(java.util.Calendar.DAY_OF_MONTH))
                            set(java.util.Calendar.HOUR_OF_DAY, 0)
                            set(java.util.Calendar.MINUTE, 0)
                            set(java.util.Calendar.SECOND, 0)
                            set(java.util.Calendar.MILLISECOND, 0)
                        }
                        customStartDate = localCal.timeInMillis
                    }
                    val sEnd = dateRangePickerState.selectedEndDateMillis
                    if (sEnd != null) {
                        val utcCal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply { timeInMillis = sEnd }
                        val localCal = java.util.Calendar.getInstance().apply {
                            timeInMillis = System.currentTimeMillis()
                            set(java.util.Calendar.YEAR, utcCal.get(java.util.Calendar.YEAR))
                            set(java.util.Calendar.MONTH, utcCal.get(java.util.Calendar.MONTH))
                            set(java.util.Calendar.DAY_OF_MONTH, utcCal.get(java.util.Calendar.DAY_OF_MONTH))
                            set(java.util.Calendar.HOUR_OF_DAY, 0)
                            set(java.util.Calendar.MINUTE, 0)
                            set(java.util.Calendar.SECOND, 0)
                            set(java.util.Calendar.MILLISECOND, 0)
                        }
                        customEndDate = localCal.timeInMillis
                    }
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

    if (showSingleDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = customSingleDate ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showSingleDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val sDate = datePickerState.selectedDateMillis
                    if (sDate != null) {
                        val utcCal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply { timeInMillis = sDate }
                        val localCal = java.util.Calendar.getInstance().apply { 
                            timeInMillis = System.currentTimeMillis()
                            set(java.util.Calendar.YEAR, utcCal.get(java.util.Calendar.YEAR))
                            set(java.util.Calendar.MONTH, utcCal.get(java.util.Calendar.MONTH))
                            set(java.util.Calendar.DAY_OF_MONTH, utcCal.get(java.util.Calendar.DAY_OF_MONTH))
                            set(java.util.Calendar.HOUR_OF_DAY, 0)
                            set(java.util.Calendar.MINUTE, 0)
                            set(java.util.Calendar.SECOND, 0)
                            set(java.util.Calendar.MILLISECOND, 0)
                        }
                        customSingleDate = localCal.timeInMillis
                    }
                    showSingleDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSingleDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEssentialCatsDialog) {
        var tempSelected by remember { mutableStateOf(essentialCatIds.toSet()) }
        AlertDialog(
            onDismissRequest = { showEssentialCatsDialog = false },
            title = { Text("Select Everyday Essentials") },
            text = {
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    categories.forEach { cat ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable {
                            tempSelected = if (tempSelected.contains(cat.id)) tempSelected - cat.id else tempSelected + cat.id
                        }.padding(vertical = 4.dp)) {
                            Checkbox(checked = tempSelected.contains(cat.id), onCheckedChange = { 
                                tempSelected = if (it) tempSelected + cat.id else tempSelected - cat.id
                            })
                            Text(cat.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateEssentialCategories(tempSelected)
                    showEssentialCatsDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEssentialCatsDialog = false }) { Text("Cancel") }
            }
        )
    }
}
