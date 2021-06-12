package com.example.fyp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fyp.adapter.MealItemAdapter
import com.example.fyp.data.local.MealDAO
import com.example.fyp.data.remote.MealService
import com.example.fyp.data.repository.MealRepository
import com.example.fyp.viewmodels.MealViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private val viewModel: MealViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = com.example.fyp.databinding.FragmentHomeBinding.inflate(layoutInflater)
        val adapter = MealItemAdapter()

        viewModel.data.observe(viewLifecycleOwner, { it ->
            it.let { resource ->
                resource.data?.let {
                    adapter.dataset = it
                    Log.e("CHECK",it.size.toString())
                }
                Log.e("TEST", resource.message.toString())
            }
        })
        binding.mealTodayRecycler.adapter = adapter
        return binding.root
    }

    companion object {

    }
}