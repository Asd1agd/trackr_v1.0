package com.example.financetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val type: String, // "Credit" or "Debit"
    val timestamp: Long,
    val categoryId: Int?,
    val note: String,
    val isSubscription: Boolean = false,
    val dueDate: Long? = null,
    val importId: Int? = null
)
