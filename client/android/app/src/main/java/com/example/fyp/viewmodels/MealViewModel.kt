package com.example.fyp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.entities.Goal
import com.example.fyp.data.entities.UserInformation
import com.example.fyp.data.repository.MealRepository
import com.example.fyp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject constructor(
    val repo: MealRepository,
) : ViewModel() {

    val todayMeals = repo.getTodayMeals()

    val calorieGoal = repo.getCalorieGoal()
    val weightGoal = repo.getWeightGoal()

    fun deleteAll() {
        viewModelScope.launch {
            repo.deleteAllMeals()
        }
    }

    fun getAllGoals(): LiveData<Resource<List<Goal>>> {
        return repo.getAllGoals()
    }

    fun getUserInformation(): UserInformation {
        return repo.getUserProfileInfo()
    }

    fun setGoal(goalType: Int, startValue: Int?, goalEndValue: Int, goalID: Int?) {

        val newGoal = Goal(goalType = goalType,
            goalStartValue = startValue ?: 0,
            goalEndValue = goalEndValue,
            goalID = goalID)

        if (newGoal.goalType == 0){
            newGoal.goalStartValue = getUserInformation().weight?.toInt()
        }


        if (newGoal.goalID != null) {
            repo.updateGoal(newGoal)
        } else {
            repo.addNewGoal(newGoal)
        }

    }


}