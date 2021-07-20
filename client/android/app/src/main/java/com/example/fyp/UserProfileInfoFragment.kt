package com.example.fyp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fyp.adapter.GoalAdapter
import com.example.fyp.databinding.FragmentUserProfileInfoBinding
import com.example.fyp.utils.Resource
import com.example.fyp.utils.Utils
import com.example.fyp.viewmodels.MealViewModel
import kotlin.math.roundToInt

class UserProfileInfoFragment : Fragment() {

    private val viewModel: MealViewModel by activityViewModels()

    lateinit var binding: FragmentUserProfileInfoBinding

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

        viewModel.weightGoal.observe(viewLifecycleOwner, {
            if (it != null) {
                val percentage = Utils.calculatePercentage((it.goalStartValue ?: 0).toFloat(),
                    it.goalEndValue.toFloat(),
                    userInfo.weight!!)
                Log.e("P",percentage.toString())
                binding.weightPercentageView.setPercentage(percentage)
                binding.weightPercentageStart.text = it.goalStartValue.toString()
                binding.weightPercentageEnd.text = it.goalEndValue.toString()
                binding.textCurrentWeight.text =
                    resources.getString(R.string.current_weight_value, userInfo.weight.toInt())
            }
        })


        binding.goalRecycler.adapter = adapter

        return binding.root
    }
}