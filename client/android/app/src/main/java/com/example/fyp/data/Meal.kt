package com.example.fyp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

enum class MealTime {
    BREAKFAST,
    LUNCH,
    DINNER
}

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey
    var id: String,
    @ColumnInfo(name = "date")
    var date: Long,
    var type: Enum<MealTime>,
    var foodList: ArrayList<String>,
    var calorieList: ArrayList<Int>
)

class Converters {
    @TypeConverter
    fun ListToString(itemList: ArrayList<String>): String {
        return Gson().toJson(itemList)
    }

    fun StringToList(string: String):ArrayList<String>{
        val listType = object:TypeToken<ArrayList<String>>(){}.type
        return Gson().fromJson(string,listType)
    }
}