package com.example.fyp.data.local

import android.content.Context
import androidx.room.*
import com.example.fyp.data.entities.Meal

@Database(entities = [Meal::class], version = 3, exportSchema = false)
@TypeConverters(Converter::class)
abstract class MealDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDAO

    companion object {

        @Volatile
        private var INSTANCE: MealDatabase? = null

        fun getInstance(context: Context): MealDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MealDatabase::class.java,
                        "meal_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}