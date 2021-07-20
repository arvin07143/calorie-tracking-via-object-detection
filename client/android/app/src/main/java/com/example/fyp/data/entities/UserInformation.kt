package com.example.fyp.data.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class UserInformation(
    val uid: String?,
    val gender: Int?,
    val height: Int?,
    val weight: Float?,
    @Json(name = "date_of_birth")
    val dateOfBirth: Date?,
)
