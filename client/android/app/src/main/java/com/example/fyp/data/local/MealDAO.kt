package com.example.fyp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fyp.data.entities.Meal
import java.util.*

@Dao
interface MealDAO {
    @Insert
    fun createMeal(meal: Meal)

    @Update
    fun updateMeal(meal: Meal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFromRemote(vararg meals: Meal)

    @Delete
    fun deleteMeal(meals: Meal)

    @Query("SELECT * FROM meals")
    fun getAllMeal(): LiveData<List<Meal>>

    @Query("SELECT * FROM meals WHERE date(mealTime/1000,'unixepoch') = date('now')")
    fun getTodayMeals(): LiveData<List<Meal>>

    //Meal TIme must be converted from milliseconds to seconds before using date() as per SQLite Documentation
    @Query("SELECT * FROM meals WHERE date(mealTime/1000,'unixepoch') = date('now') AND mealType = :mealType LIMIT 1")
    fun getTodayMealByType(mealType:Int) : LiveData<Meal>

    @Query("DELETE FROM meals")
    fun deleteAll()

}