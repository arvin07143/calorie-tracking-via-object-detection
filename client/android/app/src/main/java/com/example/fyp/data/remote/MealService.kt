package com.example.fyp.data.remote

import androidx.lifecycle.LiveData
import com.example.fyp.data.entities.Meal
import com.example.fyp.utils.Resource
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MealService {

    @GET("/users/{uid}/meals/")
    fun getAllMeals(@Path("uid") userId: String) : LiveData<Resource<List<Meal>>>

    @POST("/users/{uid}/meals/")
    fun insertMeals(@Path("uid") userId: String, @Body meal: Meal)

}