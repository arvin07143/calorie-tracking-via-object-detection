package com.example.fyp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fyp.adapter.FoodSearchItemAdapter
import com.example.fyp.databinding.FragmentAddMealCustomItemBinding


class AddMealCustomItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentAddMealCustomItemBinding.inflate(layoutInflater)

        val foodAdapter = FoodSearchItemAdapter()
        binding.customItemRecycler.adapter = foodAdapter
        // Inflate the layout for this fragment
        return binding.root
    }

}