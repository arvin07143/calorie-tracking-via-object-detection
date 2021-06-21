package com.example.fyp.objectdetection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.fyp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ObjectDetectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_detection)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.object_detection_container) as NavHostFragment
        val navController = navHostFragment.navController

        if (savedInstanceState == null) {
            navController.setGraph(R.navigation.object_detection_nav)
        }
    }

}