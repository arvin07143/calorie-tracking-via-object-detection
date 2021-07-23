package com.example.fyp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.entities.UserInformation
import com.example.fyp.data.local.MealDatabase
import com.example.fyp.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ibrahimsn.library.LiveSharedPreferences
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    val database: MealDatabase,
    val mealRepository: MealRepository,
) : ViewModel() {

    private fun clearDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            database.clearAllTables()
        }
    }

    private fun clearSharedPreference() {
        mealRepository.clearSharedPreference()
    }

    fun logOut() {
        clearDatabase()
        clearSharedPreference()
    }

    fun updateUserInformation(gender: Int?, height: Int?, weight: Float?, dateOfBirth: Date?) {
        val updateInformation = UserInformation(uid = null,
            gender = gender,
            height = height,
            weight = weight,
            dateOfBirth = dateOfBirth)

        mealRepository.updateUserProfileInfo(updateInformation)
    }

    fun getLiveSharedPreference(): LiveSharedPreferences {
        return mealRepository.getLiveSharedPreference()
    }

}