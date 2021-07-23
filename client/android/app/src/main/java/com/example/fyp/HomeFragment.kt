package com.example.fyp

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.example.fyp.adapter.MealItemAdapter
import com.example.fyp.data.entities.MealItem
import com.example.fyp.databinding.FragmentHomeBinding
import com.example.fyp.databinding.GoalDialogBinding
import com.example.fyp.utils.Utils
import com.example.fyp.viewmodels.MealViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: MealViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_nav, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.overflow = false
        val livePreference = viewModel.getLiveSharedPreference()
        val breakfastAdapter = MealItemAdapter()
        val lunchAdapter = MealItemAdapter()
        val dinnerAdapter = MealItemAdapter()

        var maxCalories = 2500
        var percentage = 0
        var totalCalories = 0.0
        livePreference.getInt(key = "gender", defaultValue = 0).observe(viewLifecycleOwner, {
            maxCalories = when (it) {
                0 -> 2500
                1 -> 2000
                else -> 2500
            }
        })
        binding.caloriePercentageEnd.text = maxCalories.toString()

        viewModel.calorieGoal.observe(viewLifecycleOwner, {
            if (it != null) {
                maxCalories = it.goalEndValue

                percentage = Utils.calculatePercentage(minVal = 0F,
                    maxVal = maxCalories.toFloat(),
                    currentVal = totalCalories.toFloat())

                updateCalorieUI(percentage,
                    endValue = it.goalEndValue,
                    currentCalorie = totalCalories.toInt())
            }
        })

        binding.homeProfileName.text = getString(R.string.welcome,
            FirebaseAuth.getInstance().currentUser?.displayName)

        val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        binding.homeDate.text = formatter.format(LocalDate.now())

        viewModel.todayMeals.observe(viewLifecycleOwner, { resource ->
            resource.let {
                for (meal in it) {
                    totalCalories += getTotalCalories(mealContent = meal.mealContent)
                    when (meal.mealType) {
                        0 -> breakfastAdapter.dataset = meal
                        1 -> lunchAdapter.dataset = meal
                        else -> dinnerAdapter.dataset = meal
                    }
                }

                percentage = Utils.calculatePercentage(minVal = 0F,
                    maxVal = maxCalories.toFloat(),
                    currentVal = totalCalories.toFloat())

                updateCalorieUI(percentage,totalCalories.toInt(), maxCalories)
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

        binding.btnAddBreakfast.setOnClickListener {
            showAddMealDialog(0, mealTypeList[0])
        }

        binding.btnAddLunch.setOnClickListener {
            showAddMealDialog(1, mealTypeList[1])
        }

        binding.btnAddDinner.setOnClickListener {
            showAddMealDialog(2, mealTypeList[2])
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
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.confirm)) { dialog, _ ->
                val goalType = when (binding.goalRadioGroup.checkedRadioButtonId) {
                    R.id.radio_weight_goal -> 0
                    R.id.radio_calorie_goal -> 1
                    else -> 2
                }

                val goalEndValue = binding.goalValueInput.editText?.text.toString().toInt()

                val currentGoal = when (goalType) {
                    0 -> viewModel.weightGoal
                    1 -> viewModel.calorieGoal
                    else -> TODO()
                }

                currentGoal.observeOnce(viewLifecycleOwner, {
                    if (it != null) {
                        viewModel.setGoal(goalType, null, goalEndValue, it.goalID)
                    } else {
                        viewModel.setGoal(goalType, null, goalEndValue, null)
                    }
                    dialog.dismiss()
                })
            }
            .setView(binding.root)
            .show()
    }

    private fun getTotalCalories(mealContent: MutableList<MealItem>): Double {
        return mealContent.sumOf { it.calories.toDouble() }
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    private fun updateCalorieUI(percentage: Int, currentCalorie: Int, endValue: Int) {
        binding.caloriePercentageView.setPercentage(percentage)
        binding.caloriePercentageEnd.text = endValue.toString()
        binding.txtCurrentCalories.text =
            resources.getString(R.string.current_calorie_value, currentCalorie)

        if (percentage > 100) {
            binding.overflow = true
            binding.caloriePercentageView.setPercentage(100)
        } else {
            binding.overflow = false
            binding.caloriePercentageView.setPercentage(percentage)
        }
    }
}