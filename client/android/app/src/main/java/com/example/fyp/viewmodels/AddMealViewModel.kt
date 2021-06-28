package com.example.fyp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.entities.Meal
import com.example.fyp.data.entities.MealItem
import com.example.fyp.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMealViewModel @Inject constructor(val mealRepo: MealRepository) : ViewModel() {

    val currentMealType = MutableLiveData<Int>()
    lateinit var currentMeal: LiveData<Meal>


    val searchStringLiveData = MutableLiveData<String>()


    init {
        searchStringLiveData.value = ""
        currentMealType.value = -1
    }

    fun addMealFromSearch(mealItem: MealItem) {
        currentMeal.value?.let { mealRepo.addItemToMeal(currentMeal = it,addedItem = listOf(mealItem)) }

    }

    fun getCurrentMeal(mealType: Int): LiveData<Meal> {
        return mealRepo.getTodayMealWithType(mealType)
    }

    fun addNewMeal() {
        viewModelScope.launch {
            Log.e("CREATE MEAL", "CALLED")
            currentMealType.value?.let { mealRepo.createNewMeal(it, mealContent = mutableListOf()) }
        }
    }

    fun setMealType(mealType: Int) {
        currentMealType.value = mealType
    }

}