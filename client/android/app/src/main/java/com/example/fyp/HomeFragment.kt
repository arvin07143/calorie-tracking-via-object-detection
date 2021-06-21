package com.example.fyp
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.fyp.adapter.MealItemAdapter
import com.example.fyp.utils.Resource
import com.example.fyp.viewmodels.MealViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: MealViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = com.example.fyp.databinding.FragmentHomeBinding.inflate(layoutInflater)
        val breakfastAdapter = MealItemAdapter()
        val lunchAdapter = MealItemAdapter()
        val dinnerAdapter = MealItemAdapter()

        viewModel.data.observe(viewLifecycleOwner, { resource ->
            if (resource.status == Resource.Status.SUCCESS) {
                resource.data?.let {
                    Log.e("DATASIZE", it.size.toString())
                    for (meal in it) {
                        Log.e("MEAL", meal.mealType.toString())
                        when (meal.mealType) {
                            0 -> breakfastAdapter.dataset.add(meal)
                            1 -> lunchAdapter.dataset.add(meal)
                            else -> dinnerAdapter.dataset.add(meal)
                        }
                    }
                    breakfastAdapter.notifyDataSetChanged()
                    lunchAdapter.notifyDataSetChanged()
                    dinnerAdapter.notifyDataSetChanged()
                }
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e("RESOURCE", resource.message.toString())
            }
        })

        binding.breakfastRecycler.adapter = breakfastAdapter
        binding.lunchRecycler.adapter = lunchAdapter
        binding.dinnerRecycler.adapter = dinnerAdapter

        val mealTypeList = arrayOf("Breakfast", "Lunch", "Dinner")
        binding.fabAddMeal.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Meal Type")
                .setItems(mealTypeList) { _, which ->
                    when (which) {
                        0 -> showAddMealDialog(0)
                        1 -> showAddMealDialog(1)
                        else -> showAddMealDialog(2)
                    }
                }
                .show()
        }

        val goalTypeList = arrayOf("Weight Goal", "Calorie Goal")
        binding.fabAddGoal.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Meal Type")
                .setItems(goalTypeList) { _, which ->
                    when (which) {
                        0 -> Log.e("LOG", "Weight Goal")
                        else -> Log.e("LOG", "Calorie Goal")
                    }
                }
                .show()
        }
        return binding.root
    }

    private fun showAddMealDialog(mealType: Int) {
        val action = HomeFragmentDirections.actionHomeToAddMealFragment(mealType)
        findNavController().navigate(action)
    }
}