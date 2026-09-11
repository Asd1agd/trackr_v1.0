package com.example.financetracker

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
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
import java.util.Calendar

class AllowanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AllowanceWidget()
}

class AllowanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = FinanceDatabase.getDatabase(context)
        val transactionDao = db.transactionDao()
        val categoryDao = db.categoryDao()
        val budgetDao = db.budgetDao()

        // ── Calendar setup (identical to DashboardScreen) ──
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val remainingDaysIncludingToday = (maxDays - currentDay + 1).coerceAtLeast(1)

        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val monthStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // ── Data fetch using SYNCHRONOUS queries (no Flow caching) ──
        val transactions = transactionDao.getTransactionsSinceSync(monthStart)
        val cats = categoryDao.getCategoriesSync()
        val budgets = budgetDao.getBudgetsForMonthSync(currentMonth, currentYear)

        // ── Essential category IDs: read from SharedPreferences just like Dashboard ──
        val financePrefs = context.getSharedPreferences("finance_prefs", Context.MODE_PRIVATE)
        val savedEssentialIds = financePrefs.getStringSet("essential_cats", emptySet())
            ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        val essentialCatIds = if (savedEssentialIds.isEmpty()) {
            cats.filter { it.isEssential }.map { it.id }
        } else {
            savedEssentialIds.toList()
        }

        // ── EXACT Dashboard calculation (DashboardScreen.kt lines 98-114) ──
        val essentialBudget = budgets
            .filter { it.categoryId in essentialCatIds }
            .sumOf { it.amount }

        val essentialSpentTillYesterday = transactions.filter {
            it.timestamp >= monthStart && it.timestamp < todayStart &&
                    it.categoryId in essentialCatIds && it.type == "Debit"
        }.sumOf { it.amount }

        val dailyBudget = (essentialBudget - essentialSpentTillYesterday) / remainingDaysIncludingToday

        val essentialSpentToday = transactions.filter {
            it.timestamp >= todayStart && it.categoryId in essentialCatIds && it.type == "Debit"
        }.sumOf { it.amount }

        val todaysAllowance = dailyBudget - essentialSpentToday
        val dailyBudgetMax = dailyBudget.coerceAtLeast(1.0)

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
            text = if (todaysAllowance >= 0) "₹${"%.2f".format(todaysAllowance)}"
                   else "₹${"%.2f".format(todaysAllowance)}",
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
