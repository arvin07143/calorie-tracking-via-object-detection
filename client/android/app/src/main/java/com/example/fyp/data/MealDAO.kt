package com.example.fyp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MealDAO {
    @Insert
    fun importMeals(vararg meals:Meal)

    @Delete
    fun deleteMeal(meals: Meal)

    @Query ("SELECT * FROM meals WHERE date =:time")
    fun getMeals(time: Long)
}