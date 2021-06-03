package com.example.fyp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fyp.adapter.MealItemAdapter


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = com.example.fyp.databinding.FragmentHomeBinding.inflate(layoutInflater)

        val adapter = MealItemAdapter()
        binding.mealTodayRecycler.adapter = adapter
        return binding.root
    }

    companion object {

    }
}