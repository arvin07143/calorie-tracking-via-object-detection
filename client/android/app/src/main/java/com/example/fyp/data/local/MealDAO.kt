package com.example.fyp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.fyp.data.entities.Meal
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDAO {
    @Insert
    fun createMeal(meal: Meal)

    @Insert
    fun insertFromRemote(vararg meals: Meal)

    @Delete
    fun deleteMeal(meals: Meal)

    @Query("SELECT * FROM meals LIMIT 1")
    fun getMeal(): LiveData<List<Meal>>

}