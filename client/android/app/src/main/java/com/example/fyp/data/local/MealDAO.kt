package com.example.fyp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fyp.data.entities.Meal

@Dao
interface MealDAO {
    @Insert
    fun createMeal(meal: Meal)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFromRemote(vararg meals: Meal)

    @Delete
    fun deleteMeal(meals: Meal)

    @Query("SELECT * FROM meals")
    fun getMeal(): LiveData<List<Meal>>

}