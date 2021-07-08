package com.example.fyp.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.fyp.data.entities.MealItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converter {
    @TypeConverter
    fun listToString(itemList: List<String>): String {
        return Gson().toJson(itemList)
    }

    @TypeConverter
    fun mealListToString(itemList: MutableList<MealItem>): String {
        return Gson().toJson(itemList)
    }

    @TypeConverter
    fun stringToList(string: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(string, listType)
    }

    @TypeConverter
    fun stringToMealList(string: String): MutableList<MealItem> {
        val listType = object : TypeToken<MutableList<MealItem>>() {}.type
        return Gson().fromJson(string, listType)
    }

    @TypeConverter
    fun intListToString(itemList: List<Int>): String {
        return Gson().toJson(itemList)
    }

    @TypeConverter
    fun stringToIntList(string: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(string, listType)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun timeToLong(time: Date): Long {
        return time.time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun longToTime(time: Long): Date {
        return Date(time)
    }
}