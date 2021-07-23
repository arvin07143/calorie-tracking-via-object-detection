package com.example.fyp.utils

import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
import java.util.*
import kotlin.math.pow

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

    fun calculatePercentage(minVal: Float, maxVal: Float, currentVal: Float): Int {
        return (((currentVal - minVal) / (maxVal - minVal)) * 100).toInt()
    }

    fun calculateBMI(weight: Float, height: Int): Float {
        Log.e("weight",weight.toString())
        Log.e("height",height.toString())
        return (weight / ((height.toFloat() / 100)).pow(2))
    }

    enum class BMIType {
        Underweight,
        Normal,
        Overweight,
        Obese
    }

    fun bmiLookUp(bmi: Float): BMIType {
        return when {
            bmi > 30 -> {
                BMIType.Obese
            }
            bmi > 25 -> {
                BMIType.Overweight
            }
            bmi > 18.5 -> {
                BMIType.Normal
            }
            else -> {
                BMIType.Underweight
            }
        }
    }
}