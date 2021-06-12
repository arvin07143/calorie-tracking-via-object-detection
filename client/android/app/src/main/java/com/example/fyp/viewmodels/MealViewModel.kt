package com.example.fyp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.fyp.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject constructor(
    repo: MealRepository
) : ViewModel() {

    val data = repo.getAllMeals()

}