package com.example.fyp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey
    @Json(name = "goal_type")
    val goalType: Int, //0 = weight 1 = calorie
    @Json(name = "goal_start")
    var goalStartValue: Int?,
    @Json(name = "goal_end")
    val goalEndValue: Int,
    @Json(name = "goal_id")
    var goalID: Int?,
)

