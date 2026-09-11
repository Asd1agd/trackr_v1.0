package com.example.financetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("DELETE FROM goals WHERE importId = :importId")
    suspend fun deleteByImportId(importId: Int)
    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<Goal>>

    @Insert
    suspend fun insert(goal: Goal)

    @Update
    suspend fun update(goal: Goal)
    
    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteById(id: Int)
}
