package com.example.fyp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.fyp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (savedInstanceState == null){
            navController.setGraph(R.navigation.main_nav)
        }

        setupActionBarWithNavController(navController)


        val bottomNav: BottomNavigationView = binding.mainBottomNav
        bottomNav.setupWithNavController(navController)

//        val mUser = FirebaseAuth.getInstance().currentUser
//        var tokenCode = ""
//        mUser?.getIdToken(true)?.addOnCompleteListener {
//            tokenCode = if (it.isSuccessful) {
//                it.result!!.token!!.toString()
//
//            } else {
//                ""
//            }
//        }
//

        setContentView(binding.root)
    }

}