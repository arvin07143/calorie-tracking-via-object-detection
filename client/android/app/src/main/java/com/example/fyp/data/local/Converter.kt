package com.example.fyp.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class Converter {
    @TypeConverter
    fun ListToString(itemList: List<String>): String {
        return Gson().toJson(itemList)
    }

    @TypeConverter
    fun StringToList(string: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(string, listType)
    }

    @TypeConverter
    fun IntListToString(itemList: List<Int>): String {
        return Gson().toJson(itemList)
    }

    @TypeConverter
    fun StringToIntList(string: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(string, listType)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun TimeToLong(time: Date): Long {
        return time.time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun LongToTime(time: Long): Date {
        return Date(time)
    }
}