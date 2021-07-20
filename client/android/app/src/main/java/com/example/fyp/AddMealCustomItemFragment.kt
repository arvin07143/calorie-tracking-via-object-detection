package com.example.fyp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fyp.adapter.OnItemClickListener
import com.example.fyp.adapter.SavedItemAdapter
import com.example.fyp.data.entities.MealItem
import com.example.fyp.databinding.FragmentAddMealCustomItemBinding
import com.example.fyp.viewmodels.AddMealViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class AddMealCustomItemFragment : Fragment() , OnItemClickListener{

    val viewModel: AddMealViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentAddMealCustomItemBinding.inflate(layoutInflater)

        val foodAdapter = SavedItemAdapter().also {
            it.onItemClickListener = this
        }

        viewModel.getSavedMeals().observe(viewLifecycleOwner, { resource ->
            Log.e("CUSTOM", "CALLED")
            resource.data?.let {
                Log.e("CUSTOM", it.size.toString())
                foodAdapter.dataset = it
            }
        })

        binding.customItemRecycler.adapter = foodAdapter
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onClick(item: MealItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Meal")
            .setPositiveButton("Add") { _, _ ->
                viewModel.addMealFromSearch(item)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}