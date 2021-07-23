package com.example.fyp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fyp.data.entities.Goal

@Dao
interface GoalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoal(goal: Goal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFromRemote(vararg goals: Goal)

    @Update
    fun updateGoal(goal: Goal)

    @Delete
    fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM goals")
    fun getAllGoals(): LiveData<List<Goal>>

    @Query("SELECT * FROM goals WHERE goalType = 0")
    fun getWeightGoal(): LiveData<Goal>

    @Query("SELECT * FROM goals WHERE goalType = 1")
    fun getCalorieGoal(): LiveData<Goal>

}