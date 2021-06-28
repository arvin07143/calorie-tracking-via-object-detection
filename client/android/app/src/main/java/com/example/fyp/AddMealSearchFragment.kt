package com.example.fyp

import android.os.Bundle
import android.util.JsonWriter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.fyp.adapter.FoodSearchItemAdapter
import com.example.fyp.adapter.OnItemClickListener
import com.example.fyp.data.entities.FoodSearchResultList
import com.example.fyp.data.entities.MealItem
import com.example.fyp.data.remote.MealService
import com.example.fyp.databinding.FragmentAddMealSearchBinding
import com.example.fyp.viewmodels.AddMealViewModel
import com.google.gson.JsonObject
import com.squareup.moshi.Json
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class AddMealSearchFragment : Fragment(),OnItemClickListener{

    @Inject lateinit var mealService:MealService

    private val addMealViewModel: AddMealViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentAddMealSearchBinding.inflate(layoutInflater)

        val adapter = FoodSearchItemAdapter().also {
            it.onItemClickListener = this
        }
        addMealViewModel.searchStringLiveData.observe(viewLifecycleOwner, {
            val call = mealService.search(it)
            call.enqueue(object : Callback<FoodSearchResultList> {
                override fun onResponse(
                    call: Call<FoodSearchResultList>,
                    response: Response<FoodSearchResultList>,
                ) {
                    if (response.isSuccessful) {
                        adapter.dataset = response.body()
                        adapter.notifyDataSetChanged()
                    }
                }
                override fun onFailure(call: Call<FoodSearchResultList>, t: Throwable) {
                    Log.e("SEARCH",t.message.toString())
                }
            })
        })


        binding.searchResultRecycler.adapter = adapter

        return binding.root
    }

    override fun addNewItem(item: MealItem) {
        addMealViewModel.addMealFromSearch(item)
    }

}