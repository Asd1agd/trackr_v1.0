package com.example.financetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val keywords: String, // Comma separated, e.g. "Zomato,Uber"
    val isEssential: Boolean = false
)
