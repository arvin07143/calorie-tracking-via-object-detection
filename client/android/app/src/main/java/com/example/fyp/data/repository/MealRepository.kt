package com.example.fyp.data.repository

import androidx.lifecycle.LiveData
import com.example.fyp.AppExecutors
import com.example.fyp.data.entities.Meal
import com.example.fyp.data.local.MealDAO
import com.example.fyp.data.remote.MealService
import com.example.fyp.utils.NetworkBoundResource
import com.example.fyp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

class MealRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val remoteSource: MealService,
    private val localSource: MealDAO
) {

    companion object {
        private val TAG = "REPO"
    }
    fun getAllMeals(): LiveData<Resource<List<Meal>>> {
        return object : NetworkBoundResource<List<Meal>, List<Meal>>(appExecutors) {
            override fun saveCallResult(item: List<Meal>) {
                localSource.insertFromRemote(*item.map{it}.toTypedArray())
            }

            override fun shouldFetch(data: List<Meal>?): Boolean {
                return false //TODO
            }

            override fun loadFromDb(): LiveData<List<Meal>> {
                return localSource.getMeal()
            }

            override fun createCall(): LiveData<Resource<List<Meal>>> {
                return remoteSource.getAllMeals("me")
            }

        }.asLiveData()

    }

    fun refreshMeals() {
        TODO()
    }
}