package com.example.fyp.utils

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout

object Utils {

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}