package com.example.financetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ImportLogDao {
    @Query("SELECT * FROM import_logs ORDER BY timestamp DESC")
    fun getAllImportLogs(): Flow<List<ImportLog>>

    @Insert
    suspend fun insert(importLog: ImportLog): Long

    @Query("DELETE FROM import_logs WHERE id = :id")
    suspend fun delete(id: Int)
}
