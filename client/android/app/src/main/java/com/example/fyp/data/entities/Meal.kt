package com.example.fyp.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@Entity(tableName = "meals")
@JsonClass(generateAdapter = true)
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    @Json(name = "meal_content")
    @Embedded
    val mealContent: MealContent,
    @Json(name = "meal_time")
    val mealTime: Date,
    @Json(name = "meal_type")
    val mealType: Int
)

@JsonClass(generateAdapter = true)
data class MealContent(
    @Json(name = "calories")
    val calorie_list: List<Int>,
    @Json(name = "food")
    val food_list: List<String>
)

enum class MealType(val type: Int) {
    BREAKFAST(0),
    LUNCH(1),
    DINNER(2)
}

