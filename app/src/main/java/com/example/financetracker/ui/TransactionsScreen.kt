package com.example.financetracker.ui
import com.example.financetracker.theme.bounceClick

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.financetracker.data.Category
import com.example.financetracker.data.Transaction
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionsScreen(viewModel: FinanceViewModel, modifier: Modifier = Modifier) {
    val transactions by viewModel.transactions.collectAsState()
    val categoryFilter by viewModel.selectedCategoryFilter.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        
        val displayTxns = if (categoryFilter != null) {
            transactions.filter { it.categoryId == categoryFilter }
        } else transactions
        
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("All Transactions" + if (categoryFilter != null) " (Filtered)" else "", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            if (categoryFilter != null) {
                TextButton(onClick = { viewModel.setCategoryFilter(null) }) { Text("Clear Filter") }
            }
        }
        
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()
        val view = androidx.compose.ui.platform.LocalView.current
        val isHapticsEnabled = com.example.financetracker.theme.LocalHapticEnabled.current
        LaunchedEffect(listState.firstVisibleItemIndex) { if(isHapticsEnabled) view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK) }
        LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
            items(displayTxns) { txn ->

                TransactionItem(
                    transaction = txn,
                    categoryName = categories.find { it.id == txn.categoryId }?.name ?: "Uncategorized",
                    onClick = { transactionToEdit = txn },
                    onDelete = { transactionToDelete = txn }
                )
            }
        }
    }

    if (transactionToEdit != null) {
        TransactionDialog(
            transaction = transactionToEdit,
            categories = categories,
            onDismiss = { transactionToEdit = null },
            onSave = { amount, type, note, catId, timestamp ->
                viewModel.updateTransaction(
                    transactionToEdit!!.copy(
                        amount = amount,
                        type = type,
                        note = note,
                        categoryId = catId,
                        timestamp = timestamp
                    )
                )
                transactionToEdit = null
            },
            onAddCategory = { newCategory ->
                viewModel.addCategory(newCategory)
            }
        )
    }

    if (transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTransaction(transactionToDelete!!)
                    transactionToDelete = null
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) { Text("No") }
            }
        )
    }
}

@Composable
fun TransactionItem(transaction: Transaction, categoryName: String, onClick: () -> Unit, onDelete: () -> Unit) {
    // 12-hour format as requested by user
    val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateString = formatter.format(Date(transaction.timestamp))
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).bounceClick { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(categoryName, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Text(transaction.note, fontSize = 14.sp, color = Color.Gray, maxLines = 1)
                Text(dateString, fontSize = 12.sp, color = Color.Gray)
                if (transaction.isSubscription) {
                    Text("Subscription", fontSize = 12.sp, color = Color.Blue, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                text = "${if (transaction.type == "Credit") "+" else "-"} ₹${transaction.amount}",
                color = if (transaction.type == "Credit") Color(0xFF4CAF50) else Color(0xFFF44336),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Row {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.padding(start = 8.dp).bounceClick { onClick() }, tint = Color.Gray)
                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.padding(start = 8.dp).bounceClick { onDelete() }, tint = Color.Red)
            }
        }
    }
}
