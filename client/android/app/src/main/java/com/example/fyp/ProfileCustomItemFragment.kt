package com.example.fyp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fyp.adapter.OnItemUpdateClickListener
import com.example.fyp.adapter.SavedItemAdapter
import com.example.fyp.data.entities.SavedItem
import com.example.fyp.databinding.EditFoodDetailsDialogBinding
import com.example.fyp.databinding.FragmentProfileCustomItemBinding
import com.example.fyp.viewmodels.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileCustomItemFragment : Fragment(), OnItemUpdateClickListener {

    lateinit var binding: FragmentProfileCustomItemBinding
    val viewModel: ProfileViewModel by activityViewModels()
    lateinit var foodAdapter: SavedItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileCustomItemBinding.inflate(layoutInflater)

        foodAdapter = SavedItemAdapter().also {
            it.onItemUpdateClickListener = this
        }

        viewModel.getSavedMeals().observe(viewLifecycleOwner, { resource ->
            Log.e("CUSTOM", "CALLED")
            resource.data?.let {
                Log.e("CUSTOM", it.size.toString())
                foodAdapter.dataset = it
            }
        })

        binding.profileTomItemRecycler.adapter = foodAdapter

        binding.addCustomItem.setOnClickListener {
            val binding = EditFoodDetailsDialogBinding.inflate(layoutInflater)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add New Custom Item")
                .setView(binding.root)
                .setPositiveButton("Confirm"){_,_ ->
                    val itemName = binding.foodNameField.editText?.text.toString()
                    val itemCalorie = binding.calorieValueField.editText?.text.toString().toFloat()
                    val savedItem = SavedItem(0,null,itemName,itemCalorie)
                    viewModel.addSavedItem(savedItem)
                }
                .setNegativeButton("Cancel"){dialog,_ -> dialog.dismiss()}
                .show()
        }


        return binding.root
    }

    override fun onClick(savedItem: SavedItem) {
        val binding = EditFoodDetailsDialogBinding.inflate(layoutInflater)
        val itemName = binding.foodNameField.editText
        itemName?.setText(savedItem.foodName)
        val itemCalorie = binding.calorieValueField.editText
        itemCalorie?.setText(savedItem.calories.toString())
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Custom Item")
            .setView(binding.root)
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Confirm"){ _, _ ->
                savedItem.calories = itemCalorie?.text.toString().toFloat()
                savedItem.foodName = itemName?.text.toString()

                viewModel.updateSavedMeal(savedItem)
                foodAdapter.notifyDataSetChanged()
            }
            .setNeutralButton("Delete"){ _, _ ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Confirm Delete?")
                    .setMessage("Are you sure that you want to delete this item? This action cannot be reverted.")
                    .setPositiveButton("Confirm"){_,_ ->
                        viewModel.deleteSavedMeal(savedItem)
                        foodAdapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("Cancel"){dialog,_ -> dialog.dismiss()}
                    .show()
            }
            .show()
    }

}