package com.example.fyp.objectdetection

import android.net.Uri
import android.os.Bundle
import android.util.Log
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


        if (!intent.getBooleanExtra("capture", true)) {
            val directions = intent.getParcelableExtra<Uri>("content")?.let {
                Log.e("TEST", it.toString())
                ObjectDetectionFragmentDirections.actionObjectDetectionFragmentToObjectDetectionResultFragment(
                    it,true)
            }
            if (directions != null) {
                navController.navigate(directions)
            }
        }


    }

}