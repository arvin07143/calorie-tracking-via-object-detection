package com.example.fyp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class MealList(
    @Json(name = "meals")
    val mealList: List<Meal>,
)

@Entity(tableName = "meals")
@JsonClass(generateAdapter = true)
data class Meal(
    @Json(name = "meal_content")
    var mealContent: MutableList<MealItem>,
    @PrimaryKey
    @Json(name = "meal_time")
    val mealTime: Date,
    @Json(name = "meal_type")
    val mealType: Int,
    var mealID: Int?,
)

@JsonClass(generateAdapter = true)
data class MealItem(
    @Json(name = "item_name")
    val foodName: String,
    @Json(name = "calories")
    val calories: Float,
)

@Entity(tableName = "saved_item")
@JsonClass(generateAdapter = true)
data class SavedItem(
    @PrimaryKey(autoGenerate = true)
    @Json(name = "saved_id")
    var id: Int,
    @Json(name = "id")
    var serverID: Int?,
    @Json(name = "item_name")
    var foodName: String,
    @Json(name = "calories")
    var calories: Float,
)




