package com.example.fyp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.fyp.adapter.GoalAdapter
import com.example.fyp.data.entities.Goal
import com.example.fyp.databinding.FragmentUserProfileInfoBinding
import com.example.fyp.databinding.GoalDialogBinding
import com.example.fyp.databinding.PickerDialogBinding
import com.example.fyp.utils.Utils
import com.example.fyp.viewmodels.MealViewModel
import com.example.fyp.viewmodels.SettingViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UserProfileInfoFragment : Fragment() {

    private val viewModel: MealViewModel by activityViewModels()
    private val settingViewModel: SettingViewModel by activityViewModels()
    lateinit var binding: FragmentUserProfileInfoBinding
    var goal = Goal(-1,0,0,null)
    var weight = 0F
    var height = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUserProfileInfoBinding.inflate(layoutInflater)
        val adapter = GoalAdapter()

        viewModel.getAllGoals().observe(viewLifecycleOwner, { resource ->
            resource.data?.let {
                adapter.dataset = it
            }
        })

        val userInfo = viewModel.getUserInformation()
        weight = userInfo.weight?:0F
        height = userInfo.height?:0

        viewModel.weightGoal.observe(viewLifecycleOwner, {
            if (it != null) {
                goal = it
                var percentage = Utils.calculatePercentage((it.goalStartValue ?: 0).toFloat(),
                    it.goalEndValue.toFloat(),
                    weight)
                Log.e("P", percentage.toString())
                if (percentage > 100) {
                    percentage = 100
                } else if (percentage < 0) {
                    percentage = 0
                }

                updateWeightUI(percentage = percentage,
                    weight = weight,
                    startValue = it.goalStartValue ?: 0,
                    endValue = it.goalEndValue)
            }
        })

        settingViewModel.getLiveSharedPreference().getFloat("weight", 0F)
            .observe(viewLifecycleOwner) {
                var percentage = Utils.calculatePercentage((goal.goalStartValue ?: 0).toFloat(),
                    goal.goalEndValue.toFloat(),
                    it)
                Log.e("P", percentage.toString())
                if (percentage > 100) {
                    percentage = 100
                } else if (percentage < 0) {
                    percentage = 0
                }
                updateWeightUI(weight = it,
                    percentage = percentage,
                    startValue = goal.goalStartValue ?: 0,
                    endValue = goal.goalEndValue)
            }

        binding.btnUpdateWeight.setOnClickListener {
            val binding = PickerDialogBinding.inflate(layoutInflater)
            binding.numberPicker.minValue = 30
            binding.numberPicker.maxValue = 150
            settingViewModel.getLiveSharedPreference().getFloat("weight", 0F).observe(viewLifecycleOwner){
                binding.numberPicker.value = it.toInt()
            }
            binding.numberPicker.setFormatter {
                resources.getString(R.string.weight_value, it)
            }
            binding.numberPicker.wrapSelectorWheel = false

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Height")
                .setView(binding.root)
                .setPositiveButton("Confirm") { dialog, _ ->
                    settingViewModel.updateUserInformation(weight = binding.numberPicker.value.toFloat(),
                        gender = null,
                        height = null,
                        dateOfBirth = null)
                    dialog.dismiss()

                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        viewModel.getLiveSharedPreference().getFloat("weight",0F).observe(viewLifecycleOwner){
            weight = it
            val bmi = Utils.calculateBMI(weight,height)
            updateBMIUI(bmi)
        }

        viewModel.getLiveSharedPreference().getInt("height",0).observe(viewLifecycleOwner){
            height = it
            val bmi = Utils.calculateBMI(weight,height)
            updateBMIUI(bmi)
        }

        binding.goalRecycler.adapter = adapter

        binding.btnUpdateGoals.setOnClickListener {
            showAddGoalDialog()
        }

        return binding.root
    }

    private fun updateBMIUI(bmi:Float){
        binding.txtBmi.text = String.format("%.2f", bmi)

        val bmiType = Utils.bmiLookUp(bmi)
        binding.bmiType.text = bmiType.name

        binding.bmiType.setTextColor(
            when (bmiType) {
                Utils.BMIType.Obese -> resources.getColor(R.color.deep_orange_500)
                Utils.BMIType.Overweight -> resources.getColor(R.color.orange_500)
                Utils.BMIType.Normal -> resources.getColor(R.color.green_500)
                else -> resources.getColor(R.color.cyan_500)
            }
        )
    }

    private fun updateWeightUI(percentage: Int, weight: Float, startValue: Int, endValue: Int) {
        binding.weightPercentageView.setPercentage(percentage)
        binding.weightPercentageStart.text = startValue.toString()
        binding.weightPercentageEnd.text = endValue.toString()
        binding.textCurrentWeight.text =
            resources.getString(R.string.current_weight_value, weight.toInt())
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
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
}