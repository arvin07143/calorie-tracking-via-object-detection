package com.example.fyp.data.remote

import androidx.lifecycle.LiveData
import com.example.fyp.data.entities.Meal
import com.example.fyp.utils.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.http.*

interface MealService {

    @GET("/users/{uid}/meals/")
    fun getAllMeals(@Path("uid") userId: String) : LiveData<Resource<List<Meal>>>

    @POST("/users/{uid}/meals/")
    fun insertMeals(@Path("uid") userId: String, @Body meal: Meal, @Header("Authorization") token:String)

}