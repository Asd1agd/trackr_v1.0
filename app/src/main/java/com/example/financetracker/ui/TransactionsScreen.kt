package com.example.financetracker.ui
import com.example.financetracker.theme.bounceClick

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.financetracker.data.Category
import com.example.financetracker.data.Transaction
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionsScreen(viewModel: FinanceViewModel, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val transactions by viewModel.transactions.collectAsState()
    val categoryFilter by viewModel.selectedCategoryFilter.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        
        var searchQuery by remember { mutableStateOf("") }
        
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("All Transactions" + if (categoryFilter != null) " (Filtered)" else "", fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (categoryFilter != null) {
                TextButton(onClick = { viewModel.setCategoryFilter(null) }) { Text("Clear Filter", maxLines = 1, overflow = TextOverflow.Ellipsis) }
            }
        }
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            placeholder = { Text("Search by category, note, or date", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            leadingIcon = { Icon(Icons.Default.Search, "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )
        
        val displayTxns = if (categoryFilter != null) {
            transactions.filter { it.categoryId == categoryFilter }
        } else transactions
        
        val filteredTxns = displayTxns.filter { txn ->
            if (searchQuery.isBlank()) return@filter true
            val catName = categories.find { it.id == txn.categoryId }?.name ?: "Uncategorized"
            val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(txn.timestamp))
            txn.note.contains(searchQuery, ignoreCase = true) ||
            catName.contains(searchQuery, ignoreCase = true) ||
            dateStr.contains(searchQuery, ignoreCase = true)
        }
        
        val sortedTxns = filteredTxns.sortedByDescending { it.timestamp }
        val groupedTxns = sortedTxns.groupBy { 
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it.timestamp))
        }
        
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()
        val view = androidx.compose.ui.platform.LocalView.current
        val isHapticsEnabled = com.example.financetracker.theme.LocalHapticEnabled.current
        LaunchedEffect(listState.firstVisibleItemIndex) { if(isHapticsEnabled) view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK) }
        
        LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
            groupedTxns.forEach { (dateStr, txnsForDate) ->
                item {
                    Text(
                        text = dateStr,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 4.dp),
                        fontSize = 14.sp
                    )
                }
                items(txnsForDate) { txn ->
                    TransactionItem(
                        transaction = txn,
                        categoryName = categories.find { it.id == txn.categoryId }?.name ?: "Uncategorized",
                        onClick = { transactionToEdit = txn },
                        onDelete = { transactionToDelete = txn }
                    )
                }
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
                viewModel.addCategory(newCategory) { added -> if (!added) android.widget.Toast.makeText(context, "Category already exists, using existing", android.widget.Toast.LENGTH_SHORT).show() }
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
    // 12-hour format time only, since date is in the section header
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeString = formatter.format(Date(transaction.timestamp))
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).bounceClick(
            onClick = onClick,
            onLongClick = onDelete
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(categoryName, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Text(transaction.note, fontSize = 14.sp, color = Color.Gray, maxLines = 1)
                Text(timeString, fontSize = 12.sp, color = Color.Gray)
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
            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.padding(start = 8.dp).bounceClick(onClick = onClick), tint = Color.Gray)
        }
    }
}
