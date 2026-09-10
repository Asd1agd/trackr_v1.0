package com.example.financetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.os.Environment
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.financetracker.data.Transaction
import com.example.financetracker.data.Goal
import com.example.financetracker.data.Budget
import org.yaml.snakeyaml.Yaml
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YamlDialog(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    var yamlText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showMessage by remember { mutableStateOf<String?>(null) }
    var showFormatPopup by remember { mutableStateOf(true) }
    
    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    yamlText = inputStream.bufferedReader().use { reader -> reader.readText() }
                }
            } catch (e: Exception) {
                showMessage = "Failed to read file: ${e.message}"
            }
        }
    }

    val defaultYaml = """
transactions:
  - amount: 100.0
    type: Debit
    note: Groceries
    category: Food
    timestamp: 1718293810
goals:
  - name: Car
    targetAmount: 50000.0
    savedAmount: 1000.0
    targetDate: 1718293810
budgets:
  - category: Food
    amount: 500.0
    month: 8
    year: 2026
""".trimIndent()

    if (showFormatPopup) {
        AlertDialog(
            onDismissRequest = { showFormatPopup = false },
            title = { Text("YAML Format Example") },
            text = {
                SelectionContainer {
                    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                        Text("You can copy the format below to use as a template:", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(defaultYaml, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFormatPopup = false }) {
                    Text("OK")
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("YAML Import / Export") },
            text = {
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    if (showMessage != null) {
                        Text(showMessage!!, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    TextField(
                        value = yamlText,
                        onValueChange = { yamlText = it },
                        label = { Text("Paste YAML here") },
                        placeholder = { Text("Enter YAML here...") },
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        maxLines = 20
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { filePickerLauncher.launch("*/*") }) {
                            Text("Load File", maxLines = 1)
                        }
                        Button(onClick = {
                            coroutineScope.launch {
                                try {
                                    val txns = viewModel.transactions.value
                                    val goals = viewModel.goals.value
                                    val budgets = viewModel.budgets.value
                                    val categories = viewModel.categories.value
                                    
                                    val map = mutableMapOf<String, Any>()
                                    map["transactions"] = txns.map { t ->
                                        val catName = categories.find { c -> c.id == t.categoryId }?.name ?: "Other"
                                        mapOf(
                                            "amount" to t.amount,
                                            "type" to t.type,
                                            "note" to t.note,
                                            "category" to catName,
                                            "timestamp" to t.timestamp
                                        )
                                    }
                                    map["goals"] = goals.map { g ->
                                        mapOf(
                                            "name" to g.name,
                                            "targetAmount" to g.targetAmount,
                                            "savedAmount" to g.currentAmount,
                                            "targetDate" to g.targetDate
                                        )
                                    }
                                    map["budgets"] = budgets.map { b ->
                                        val catName = categories.find { c -> c.id == b.categoryId }?.name ?: "Other"
                                        mapOf(
                                            "category" to catName,
                                            "amount" to b.amount,
                                            "month" to b.month,
                                            "year" to b.year
                                        )
                                    }
                                    val yamlStr = Yaml().dump(map)
                                    
                                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    val trackerDir = File(downloadsDir, "finacial tracker")
                                    if (!trackerDir.exists()) trackerDir.mkdirs()
                                    
                                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                    val file = File(trackerDir, "export_$timestamp.yaml")
                                    file.writeText(yamlStr)
                                    
                                    showMessage = "Exported to ${file.absolutePath}"
                                } catch (e: Exception) {
                                    showMessage = "Export failed: ${e.message}"
                                }
                            }
                        }) {
                            Text("Export All", maxLines = 1)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        try {
                            if (yamlText.isNotBlank()) {
                                val map = Yaml().load<Map<String, Any>>(yamlText)
                                
                                val txns = map["transactions"] as? List<Map<String, Any>>
                                txns?.forEach { tMap ->
                                    val amount = (tMap["amount"] as? Number)?.toDouble() ?: 0.0
                                    val type = (tMap["type"] as? String) ?: "Debit"
                                    val note = (tMap["note"] as? String) ?: ""
                                    val catName = (tMap["category"] as? String) ?: "Other"
                                    val ts = (tMap["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                                    
                                    var cat = viewModel.categories.value.find { it.name.equals(catName, ignoreCase = true) }
                                    if (cat == null) {
                                        viewModel.addCategory(catName)
                                        kotlinx.coroutines.delay(100) // wait for DB insert
                                        cat = viewModel.categories.value.find { it.name.equals(catName, ignoreCase = true) }
                                    }
                                    viewModel.addManualTransactionWithCategory(amount, type, note, ts, cat?.id)
                                }
                                
                                val gls = map["goals"] as? List<Map<String, Any>>
                                gls?.forEach { gMap ->
                                    val name = (gMap["name"] as? String) ?: "Unnamed"
                                    val targetAmt = (gMap["targetAmount"] as? Number)?.toDouble() ?: 0.0
                                    val savedAmt = (gMap["savedAmount"] as? Number)?.toDouble() ?: 0.0
                                    val targetDate = (gMap["targetDate"] as? Number)?.toLong() ?: 0L
                                    viewModel.addGoal(name, targetAmt, targetDate, savedAmt)
                                }
                                
                                val bdgts = map["budgets"] as? List<Map<String, Any>>
                                bdgts?.forEach { bMap ->
                                    val catName = (bMap["category"] as? String) ?: "Other"
                                    val amount = (bMap["amount"] as? Number)?.toDouble() ?: 0.0
                                    val month = (bMap["month"] as? Number)?.toInt() ?: Calendar.getInstance().get(Calendar.MONTH)
                                    val year = (bMap["year"] as? Number)?.toInt() ?: Calendar.getInstance().get(Calendar.YEAR)
                                    
                                    var cat = viewModel.categories.value.find { it.name.equals(catName, ignoreCase = true) }
                                    if (cat == null) {
                                        viewModel.addCategory(catName)
                                        kotlinx.coroutines.delay(100)
                                        cat = viewModel.categories.value.find { it.name.equals(catName, ignoreCase = true) }
                                    }
                                    cat?.let { c ->
                                        val existing = viewModel.budgets.value.find { it.categoryId == c.id && it.month == month && it.year == year }
                                        if (existing != null) {
                                            viewModel.saveBudget(existing.copy(amount = amount))
                                        } else {
                                            viewModel.saveBudget(Budget(categoryId = c.id, amount = amount, month = month, year = year))
                                        }
                                    }
                                }
                                onDismiss()
                            }
                        } catch (e: Exception) {
                            showMessage = "Import failed: ${e.message}"
                        }
                    }
                }) { Text("Import") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        )
    }
}
