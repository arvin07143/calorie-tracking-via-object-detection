package com.example.fyp.data.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.fyp.AppExecutors
import com.example.fyp.data.entities.Meal
import com.example.fyp.data.entities.MealItem
import com.example.fyp.data.entities.MealList
import com.example.fyp.data.entities.UserInformation
import com.example.fyp.data.local.MealDAO
import com.example.fyp.data.remote.MealService
import com.example.fyp.utils.NetworkBoundResource
import com.example.fyp.utils.Resource
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class MealRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val remoteSource: MealService,
    private val localSource: MealDAO,
    private val sharedPreferences: SharedPreferences,
) {

    companion object {
        private const val TAG = "REPO"
    }

    fun getAllMeals(): LiveData<Resource<List<Meal>>> {
        return object : NetworkBoundResource<List<Meal>, MealList>(appExecutors) {
            override fun saveCallResult(item: MealList) {
                localSource.insertFromRemote(*item.mealList.map { it }.toTypedArray())
            }


            override fun loadFromDb(): LiveData<List<Meal>> {
                return localSource.getAllMeal()
            }

            override fun createCall(): LiveData<Resource<MealList>> {
                Log.e("CALL", "CREATED")
                return remoteSource.getAllMeals("me")
            }

            override fun shouldFetch(data: List<Meal>?): Boolean {
                return true
            }
        }.asLiveData()
    }

    fun getTodayMeals(): LiveData<List<Meal>> {
        return localSource.getTodayMeals()
    }

    fun getTodayMealWithType(mealType: Int): LiveData<Meal> {
        return localSource.getTodayMealByType(mealType)
    }

    fun deleteAllMeals() {
        appExecutors.diskIO().execute {
            localSource.deleteAll()
        }

    }

    fun createNewMeal(mealType: Int, mealContent: MutableList<MealItem>) {
        val meal =
            Meal(mealContent = mealContent, mealTime = Date(), mealType = mealType, mealID = null)

        appExecutors.diskIO().execute {
            localSource.createMeal(meal)
        }

        val call = remoteSource.insertMeals("me", meal)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val json = JSONObject(response.body()?.string().toString())
                    meal.mealID = json.getInt("meal_id")
                    appExecutors.diskIO().execute {
                        localSource.updateMeal(meal)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("CREATE MEAL", t.message.toString())
            }

        })
    }

    fun addItemToMeal(currentMeal: Meal, addedItem: List<MealItem>) {
        currentMeal.mealContent.addAll(addedItem)
        appExecutors.diskIO().execute {
            localSource.updateMeal(currentMeal)
        }

        currentMeal.let {
            val call = remoteSource.insertMealItem(userId = "me",
                mealID = it.mealID!!,
                mealItem = addedItem)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        Log.i("ADD MEAL ITEM", "SUCCESS")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("ADD MEAL ITEM", t.message.toString())
                }

            })
        }

    }

    fun getUserProfileInfo(): UserInformation {
        val height = sharedPreferences.getInt("height", 0)
        val weight = sharedPreferences.getFloat("weight", 0F)
        val dob = sharedPreferences.getLong("dob", 0)

        return UserInformation(weight = weight,
            height = height,
            dob = Date(dob),
            gender = 0,
            uid = "")
    }
}