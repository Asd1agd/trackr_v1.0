package com.example.financetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "import_logs")
data class ImportLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filename: String,
    val timestamp: Long
)
