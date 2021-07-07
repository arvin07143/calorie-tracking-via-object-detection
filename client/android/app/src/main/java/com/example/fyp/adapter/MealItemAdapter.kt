package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R
import com.example.fyp.data.entities.Meal
import java.util.*

class MealItemAdapter : RecyclerView.Adapter<MealItemAdapter.ItemViewHolder>() {

    var dataset =
        Meal(mealContent = mutableListOf(), mealTime = Date(), mealType = -1, mealID = null)
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    class ItemViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val itemName: TextView = itemview.findViewById(R.id.list_item_name)
        val itemCalories: TextView = itemview.findViewById(R.id.list_item_calorie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.meal_item_list, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        dataset.mealContent.let {
            holder.itemName.text = it[position].foodName
            holder.itemCalories.text = it[position].calories.toString()
        }
    }

    override fun getItemCount(): Int {
        return dataset.mealContent.size
    }
}