package com.example.fyp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.fyp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (savedInstanceState == null) {
            navController.setGraph(R.navigation.main_nav)
        }

        val topNav = binding.topAppBar
        setSupportActionBar(topNav)
        setupActionBarWithNavController(navController)
//
//        val bottomNav: BottomNavigationView = binding.mainBottomNav
//        bottomNav.setupWithNavController(navController)

        setContentView(binding.root)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_nav_host_fragment)
        return navController.navigateUp(AppBarConfiguration(setOf(R.id.home_nav)))
                || super.onSupportNavigateUp()
    }

}