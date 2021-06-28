package com.example.fyp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fyp.data.entities.Meal

@Database(entities = [Meal::class], version = 9, exportSchema = false)
@TypeConverters(Converter::class)
abstract class MealDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDAO

    companion object {

    }
}