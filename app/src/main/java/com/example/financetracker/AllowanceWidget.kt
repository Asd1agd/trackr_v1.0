package com.example.financetracker

import android.content.Context
import android.content.Intent
import android.content.ComponentName
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import androidx.glance.unit.ColorProvider
import androidx.glance.Button
import androidx.glance.appwidget.action.actionStartActivity
import com.example.financetracker.data.FinanceDatabase
import kotlinx.coroutines.flow.first
import java.util.Calendar
import androidx.compose.runtime.*

class AllowanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AllowanceWidget()
}

class AllowanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = FinanceDatabase.getDatabase(context)
        val transactionDao = db.transactionDao()
        val categoryDao = db.categoryDao()
        val budgetDao = db.budgetDao()
        
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val remainingDaysIncludingToday = (maxDays - currentDay + 1).coerceAtLeast(1)

        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfMonth = cal.timeInMillis

        val endCal = Calendar.getInstance()
        endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH))
        endCal.set(Calendar.HOUR_OF_DAY, 23)
        endCal.set(Calendar.MINUTE, 59)
        endCal.set(Calendar.SECOND, 59)
        endCal.set(Calendar.MILLISECOND, 999)
        val endOfMonth = endCal.timeInMillis

        val todayCal = Calendar.getInstance()
        todayCal.set(Calendar.HOUR_OF_DAY, 0)
        todayCal.set(Calendar.MINUTE, 0)
        todayCal.set(Calendar.SECOND, 0)
        todayCal.set(Calendar.MILLISECOND, 0)
        val todayStart = todayCal.timeInMillis

        val txns = transactionDao.getTransactionsBetween(startOfMonth, endOfMonth).first()
        val cats = categoryDao.getAllCategories().first()
        val budgets = budgetDao.getBudgetsForMonth(currentMonth, currentYear).first()

        val essentialCatIds = cats.filter { it.isEssential }.map { it.id }.toSet()
        val essentialBudget = budgets.filter { it.categoryId in essentialCatIds }.sumOf { it.amount }
        
        val essentialSpentToday = txns.filter { 
            it.timestamp >= todayStart && it.categoryId in essentialCatIds && it.type == "Debit" 
        }.sumOf { it.amount }

        var todaysAllowance = 0.0
        var dailyBudgetMax = 1.0

        if (budgets.isNotEmpty()) {
            val essentialSpentTillYesterday = txns.filter { 
                it.timestamp < todayStart && it.categoryId in essentialCatIds && it.type == "Debit" 
            }.sumOf { it.amount }

            val dailyBudget = (essentialBudget - essentialSpentTillYesterday) / remainingDaysIncludingToday
            dailyBudgetMax = dailyBudget.coerceAtLeast(1.0)
            
            todaysAllowance = dailyBudget - essentialSpentToday
        } else {
            val totalIncome = txns.filter { it.type == "Credit" }.sumOf { it.amount }
            val totalExpenses = txns.filter { it.type == "Debit" }.sumOf { it.amount }
            val remaining = totalIncome - totalExpenses
            
            val dailyBudget = remaining / remainingDaysIncludingToday
            dailyBudgetMax = dailyBudget.coerceAtLeast(1.0)
            
            todaysAllowance = remaining / remainingDaysIncludingToday
        }
        
        provideContent {
            GlanceContent(context, todaysAllowance, essentialSpentToday, dailyBudgetMax)
        }
    }
}

@Composable
fun GlanceContent(context: Context, todaysAllowance: Double, spentToday: Double, maxBudget: Double) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("openAddDialog", true)
    }

    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(Color(0xFF1C1C1E))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Today's Allowance",
            style = TextStyle(
                color = ColorProvider(Color.White),
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = if (todaysAllowance >= 0) "₹${"%.2f".format(todaysAllowance)}" else "₹${"%.2f".format(todaysAllowance)}",
            style = TextStyle(
                color = ColorProvider(if (todaysAllowance >= 0) Color(0xFF01D475) else Color.Red),
                fontWeight = FontWeight.Bold
            ),
            modifier = GlanceModifier.padding(top = 4.dp, bottom = 8.dp)
        )
        
        androidx.glance.appwidget.LinearProgressIndicator(
            progress = (spentToday / maxBudget).toFloat().coerceIn(0f, 1f),
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 16.dp),
            color = ColorProvider(Color(0xFFFF4081)),
            backgroundColor = ColorProvider(Color.DarkGray)
        )

        Button(
            text = "+ Add Payment",
            onClick = actionStartActivity(intent)
        )
    }
}
