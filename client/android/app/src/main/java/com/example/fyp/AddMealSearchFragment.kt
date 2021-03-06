package com.example.fyp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fyp.adapter.FoodSearchItemAdapter
import com.example.fyp.adapter.OnItemClickListener
import com.example.fyp.data.entities.FoodSearchResultList
import com.example.fyp.data.entities.MealItem
import com.example.fyp.data.remote.MealService
import com.example.fyp.databinding.FragmentAddMealSearchBinding
import com.example.fyp.viewmodels.AddMealViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class AddMealSearchFragment : Fragment(), OnItemClickListener,
    AddMealFragmentDialog.SearchListener {

    @Inject
    lateinit var mealService: MealService
    lateinit var binding: FragmentAddMealSearchBinding
    lateinit var adapter: FoodSearchItemAdapter

    private val addMealViewModel: AddMealViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddMealSearchBinding.inflate(layoutInflater)

        adapter = FoodSearchItemAdapter().also {
            it.onItemClickListener = this
        }

        binding.searchResultRecycler.adapter = adapter

        return binding.root
    }

    override fun onClick(item: MealItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Meal")
            .setPositiveButton("Add") { _, _ ->
                addMealViewModel.addMealFromSearch(item)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun search(searchTerm: String) {
        binding.searchResultRecycler.showShimmerAdapter()
        val call = mealService.search(searchTerm)
        call.enqueue(object : Callback<FoodSearchResultList> {
            override fun onResponse(
                call: Call<FoodSearchResultList>,
                response: Response<FoodSearchResultList>,
            ) {
                if (response.isSuccessful) {
                    adapter.dataset = response.body()
                    adapter.notifyDataSetChanged()
                    binding.searchResultRecycler.hideShimmerAdapter()
                }
            }

            override fun onFailure(call: Call<FoodSearchResultList>, t: Throwable) {
                Log.e("SEARCH", t.message.toString())
            }
        })
    }

}

