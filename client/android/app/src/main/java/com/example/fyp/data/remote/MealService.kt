package com.example.fyp.data.remote

import androidx.lifecycle.LiveData
import com.example.fyp.data.entities.FoodSearchResultList
import com.example.fyp.data.entities.Meal
import com.example.fyp.data.entities.MealItem
import com.example.fyp.data.entities.MealList
import com.example.fyp.utils.Resource
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MealService {

    @GET("/users/{uid}/meals/")
    fun getAllMeals(@Path("uid") userId: String): LiveData<Resource<MealList>>

    @POST("/users/{uid}/meals/")
    fun insertMeals(@Path("uid") userId: String, @Body meal: Meal): Call<ResponseBody>

    @GET("/nutrition/search/{search_term}")
    fun search(@Path(value = "search_term") searchString: String): Call<FoodSearchResultList>

    @POST("/users/{uid}/{meal_id}/")
    fun insertMealItem(
        @Path(value = "uid") userId: String,
        @Path(value = "meal_id") mealID: Int,
        @Body mealItem: List<MealItem>,
    ): Call<ResponseBody>

}