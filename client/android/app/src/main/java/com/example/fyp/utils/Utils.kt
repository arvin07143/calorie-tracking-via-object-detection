package com.example.fyp.utils

import android.content.SharedPreferences
import android.util.Patterns
import java.util.*

object Utils {

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordSame(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun saveDataToSharedPreference(
        sharedPreferences: SharedPreferences,
        uid: String,
        gender: Int,
        height: Int,
        weight: Float,
        date: Date,
    ) {
        with(sharedPreferences.edit()) {
            putString("uid", uid)
            putLong("dob", date.time)
            putInt("height", height)
            putFloat("weight", weight)
            putInt("gender", gender)
            putBoolean("SIGNED IN", true)
            apply()
        }
    }
}