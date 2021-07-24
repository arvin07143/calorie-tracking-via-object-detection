package com.example.fyp.data.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.fyp.AppExecutors
import com.example.fyp.data.entities.*
import com.example.fyp.data.local.GoalDAO
import com.example.fyp.data.local.MealDAO
import com.example.fyp.data.remote.MealService
import com.example.fyp.data.remote.WebAPI
import com.example.fyp.utils.NetworkBoundResource
import com.example.fyp.utils.Resource
import me.ibrahimsn.library.LiveSharedPreferences
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
    private val webAPI: WebAPI,
    private val localSource: MealDAO,
    private val goalSource: GoalDAO,
    private val sharedPreferences: SharedPreferences,
) {

    companion object {
        private const val TAG = "REPO"
    }

    private val liveSharedPreferences = LiveSharedPreferences(sharedPreferences)

    fun getLiveSharedPreference(): LiveSharedPreferences {
        return liveSharedPreferences
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

    fun getAllGoals(): LiveData<Resource<List<Goal>>> {
        return object : NetworkBoundResource<List<Goal>, List<Goal>>(appExecutors) {
            override fun saveCallResult(item: List<Goal>) {
                goalSource.insertFromRemote(*item.toTypedArray())
            }

            override fun shouldFetch(data: List<Goal>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Goal>> {
                return goalSource.getAllGoals()
            }

            override fun createCall(): LiveData<Resource<List<Goal>>> {
                return remoteSource.getGoalFromRemote("me")
            }

        }.asLiveData()
    }

    fun getSavedItems(): LiveData<Resource<List<SavedItem>>> {
        return object : NetworkBoundResource<List<SavedItem>, List<SavedItem>>(appExecutors) {
            override fun saveCallResult(item: List<SavedItem>) {
                localSource.deleteAllSavedItems()
                for (el in item) {
                    el.serverID = el.id
                    el.id = 0
                }
                localSource.insertSavedFromRemote(*item.toTypedArray())
            }

            override fun shouldFetch(data: List<SavedItem>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<SavedItem>> {
                return localSource.getSavedItems()
            }

            override fun createCall(): LiveData<Resource<List<SavedItem>>> {
                return remoteSource.getSavedItemsFromRemote("me")
            }

        }.asLiveData()
    }

    fun addItemToMeal(currentMeal: Meal, addedItem: List<MealItem>) {
        currentMeal.mealContent.addAll(addedItem)
        appExecutors.diskIO().execute {
            localSource.updateMeal(currentMeal)
        }

        currentMeal.let {
            it.mealID?.let { it1 ->
                remoteSource.insertMealItem(userId = "me",
                    mealID = it1,
                    mealItem = addedItem)
            }?.enqueue(object : Callback<ResponseBody> {
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

    fun addNewGoal(goal: Goal) {
        appExecutors.diskIO().execute {
            goalSource.insertGoal(goal = goal)
        }

        val call = remoteSource.newGoal(userId = "me", goal = goal)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val json = JSONObject(response.body()?.string().toString())
                    goal.goalID = json.getInt("goal_id")
                    appExecutors.diskIO().execute {
                        goalSource.updateGoal(goal)
                    }

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("ADD GOAL", t.message.toString())
            }

        })
    }

    fun updateGoal(goal: Goal) {
        appExecutors.diskIO().execute {
            goalSource.updateGoal(goal)
        }

        val call = remoteSource.updateGoal(userId = "me", goalID = goal.goalID!!, goal)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("ADD NEW GOAL", t.message.toString())
            }

        })
    }

    fun getCalorieGoal(): LiveData<Goal> {
        return goalSource.getCalorieGoal()
    }

    fun getWeightGoal(): LiveData<Goal> {
        return goalSource.getWeightGoal()
    }

    fun getUserProfileInfo(): UserInformation {
        val height = sharedPreferences.getInt("height", 0)
        val weight = sharedPreferences.getFloat("weight", 0F)
        val dob = sharedPreferences.getLong("dob", 0)

        return UserInformation(weight = weight,
            height = height,
            dateOfBirth = Date(dob),
            gender = 0,
            uid = "")
    }

    fun updateSavedMeal(savedItem: SavedItem) {
        appExecutors.diskIO().execute {
            localSource.updateSavedItem(savedItem)
        }

        if (savedItem.serverID == null) {
            remoteSource.insertSavedItemToRemote(userId = "me", savedItem)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>,
                    ) {
                        if (response.isSuccessful) {
                            val json = JSONObject(response.body()?.string().toString())
                            savedItem.serverID = json.getInt("item_id")
                            appExecutors.diskIO().execute {
                                localSource.updateSavedItem(savedItem)
                            }
                            remoteSource.updateSavedItem(userId = "me",
                                savedItemID = savedItem.serverID!!,
                                savedItem = savedItem)
                                .enqueue(object : Callback<ResponseBody> {
                                    override fun onResponse(
                                        call: Call<ResponseBody>,
                                        response: Response<ResponseBody>,
                                    ) {
                                        Log.e("Update Saved Item", "Success")
                                    }

                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        Log.e("Update Saved Item", t.message.toString())
                                    }

                                })
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("SERVER", t.message.toString())
                    }

                })
        } else {
            remoteSource.updateSavedItem(userId = "me",
                savedItemID = savedItem.serverID!!,
                savedItem = savedItem)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>,
                    ) {
                        Log.e("Update Saved Item", "Success")
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("Update Saved Item", t.message.toString())
                    }

                })
        }
    }

    fun updateUserProfileInfo(userInformation: UserInformation) {
        webAPI.updateUserInformation(userInformation, "me")
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("UPDATE", t.message.toString())
                }
            })

        with(sharedPreferences.edit()) {
            userInformation.uid?.let {
                putString("uid", it)
            }
            userInformation.dateOfBirth?.let {
                putLong("dob", it.time)
            }
            userInformation.height?.let {
                putInt("height", it)
            }
            userInformation.weight?.let {
                putFloat("weight", it)
            }
            userInformation.gender?.let {
                putInt("gender", it)
            }
            apply()
        }
    }

    fun clearSharedPreference() {
        sharedPreferences.edit()
            .putString("uid", "")
            .putLong("dob", 0)
            .putInt("height", 0)
            .putFloat("weight", 0F)
            .putInt("gender", 0)
            .putBoolean("SIGNED IN", false)
            .apply()
    }

    fun removeSavedMeal(savedItem: SavedItem) {
        appExecutors.diskIO().execute {
            localSource.deleteSavedItem(savedItem)
        }
    }

    fun addSavedItem(savedItem: SavedItem) {
        appExecutors.diskIO().execute {
            val id = localSource.insertNewSaved(savedItem)
            savedItem.id = id.toInt()
        }

        remoteSource.insertSavedItemToRemote(userId = "me", savedItem)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        val json = JSONObject(response.body()?.string().toString())
                        savedItem.serverID = json.getInt("item_id")
                        appExecutors.diskIO().execute {
                            localSource.updateSavedItem(savedItem)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("SERVER", t.message.toString())
                }

            })
    }
}