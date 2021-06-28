package com.example.fyp.data.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodSearchResultList(
    val total: Int,
    @Json(name="results")
    val list: List<FoodSearchResult>,
)

@JsonClass(generateAdapter = true)
data class FoodSearchResult(
    @Json(name = "item_name")
    val itemName: String,
    @Json(name = "nf_calories")
    val itemCalories: Float,
)
