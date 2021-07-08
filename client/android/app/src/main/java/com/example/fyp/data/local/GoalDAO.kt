package com.example.fyp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fyp.data.entities.Goal

@Dao
interface GoalDAO {
    @Insert
    fun insertGoal(goal: Goal)

    @Update
    fun updateGoal(goal: Goal)

    @Delete
    fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM goals")
    fun getAllGoals(): LiveData<List<Goal>>

}