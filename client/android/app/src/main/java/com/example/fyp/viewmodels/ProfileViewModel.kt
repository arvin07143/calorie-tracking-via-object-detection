package com.example.fyp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.fyp.data.entities.SavedItem
import com.example.fyp.data.repository.MealRepository
import com.example.fyp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(val mealRepository: MealRepository) : ViewModel() {

    fun getSavedMeals(): LiveData<Resource<List<SavedItem>>> {
        return mealRepository.getSavedItems()
    }

    fun updateSavedMeal(savedItem: SavedItem){
        mealRepository.updateSavedMeal(savedItem)
    }

    fun deleteSavedMeal(savedItem: SavedItem){
        mealRepository.removeSavedMeal(savedItem)
    }

    fun addSavedItem(savedItem: SavedItem){
        mealRepository.addSavedItem(savedItem)
    }
}