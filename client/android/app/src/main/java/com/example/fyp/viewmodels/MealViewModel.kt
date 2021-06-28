package com.example.fyp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject constructor(
    val repo: MealRepository
) : ViewModel() {

    val todayMeals = repo.getTodayMeals()

    fun deleteAll(){
        viewModelScope.launch {
            repo.deleteAllMeals()
        }
    }


}