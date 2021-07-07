package com.example.fyp

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.fyp.adapter.MealItemAdapter
import com.example.fyp.data.entities.MealItem
import com.example.fyp.databinding.FragmentHomeBinding
import com.example.fyp.databinding.GoalDialogBinding
import com.example.fyp.viewmodels.MealViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: MealViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val breakfastAdapter = MealItemAdapter()
        val lunchAdapter = MealItemAdapter()
        val dinnerAdapter = MealItemAdapter()

        binding.homeProfileName.text = getString(R.string.welcome,
            FirebaseAuth.getInstance().currentUser?.displayName)

        val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        binding.homeDate.text = formatter.format(LocalDate.now())

        viewModel.todayMeals.observe(viewLifecycleOwner, { resource ->
            var totalCalories = 0.0
            resource.let {
                for (meal in it) {
                    totalCalories += getTotalCalories(mealContent = meal.mealContent)
                    when (meal.mealType) {
                        0 -> breakfastAdapter.dataset = meal
                        1 -> lunchAdapter.dataset = meal
                        else -> dinnerAdapter.dataset = meal
                    }
                }
                updatePercentageUI(currentValue = totalCalories, maxValue = 1600.00)
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
                        0 -> showAddMealDialog(0, mealTypeList[which])
                        1 -> showAddMealDialog(1, mealTypeList[which])
                        else -> showAddMealDialog(2, mealTypeList[which])
                    }
                }
                .show()
        }

        binding.fabAddGoal.setOnClickListener {
            showAddGoalDialog()
        }
        return binding.root
    }

    private fun showAddMealDialog(mealType: Int, title: String) {
        val action = HomeFragmentDirections.actionHomeToAddMealFragment(mealType, title)
        findNavController().navigate(action)
    }

    private fun showAddGoalDialog() {
        val binding = GoalDialogBinding.inflate(layoutInflater)
        val textField = binding.goalValueInput
        binding.goalRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            textField.suffixText = when (checkedId) {
                R.id.radio_weight_goal -> "kg"
                R.id.radio_calorie_goal -> "kcal"
                else -> ""
            }
        }
        MaterialAlertDialogBuilder(requireContext())
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.confirm)) { dialog, which ->
                // Respond to positive button press
            }
            .setView(binding.root)
            .show()
    }

    private fun getTotalCalories(mealContent: MutableList<MealItem>): Double {
        return mealContent.sumOf { it.calories.toDouble() }
    }

    private fun updatePercentageUI(currentValue: Double, maxValue: Double) {
        val percentage = currentValue / maxValue
        binding.caloriePercentageView.setPercentage((percentage * 100).roundToInt())
        binding.txtCurrentCalories.text =
            resources.getString(R.string.current_calorie_value, currentValue.toInt())
    }
}