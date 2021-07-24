package com.example.fyp.data.remote

import androidx.lifecycle.LiveData
import com.example.fyp.data.entities.*
import com.example.fyp.utils.Resource
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

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

    @PUT("/users/{uid}/goals/{goal_id}")
    fun updateGoal(
        @Path(value = "uid") userId: String,
        @Path(value = "goal_id") goalID: Int,
        @Body goal: Goal,
    ): Call<ResponseBody>

    @POST("/users/{uid}/goals/")
    fun newGoal(
        @Path(value = "uid") userId: String,
        @Body goal: Goal,
    ): Call<ResponseBody>

    @GET("/users/{uid}/goals/")
    fun getGoalFromRemote(@Path(value = "uid") userId: String): LiveData<Resource<List<Goal>>>

    @GET("/users/{uid}/saved/")
    fun getSavedItemsFromRemote(@Path(value = "uid") userId: String): LiveData<Resource<List<SavedItem>>>

    @POST("/users/{uid}/saved/")
    fun insertSavedItemToRemote(
        @Path(value = "uid") userId: String,
        @Body savedItem: SavedItem,
    ): Call<ResponseBody>

    @PUT("/users/{uid}/saved/{saved_item_id}")
    fun updateSavedItem(
        @Path(value = "uid") userId: String,
        @Path(value = "saved_item_id") savedItemID: Int,
        @Body savedItem: SavedItem,
    ): Call<ResponseBody>

}