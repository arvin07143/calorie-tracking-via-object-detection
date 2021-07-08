package com.example.fyp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey
    val goalType: Int, //0 = weight 1 = calorie
    val goalStartValue: Int?,
    val goalEndValue: Int,
    val goalInterval: Float?,
    val goalID: Int?,
) {
    constructor(goalStartValue: Int, goalEndValue: Int, goalInterval: Float, goalID: Int?) : this(
        goalType = 0,
        goalStartValue,
        goalEndValue,
        goalInterval,
        goalID)

    constructor(goalEndValue: Int, goalID: Int?) : this(goalType = 1,
        goalStartValue = null,
        goalEndValue,
        null,
        goalID)
}


