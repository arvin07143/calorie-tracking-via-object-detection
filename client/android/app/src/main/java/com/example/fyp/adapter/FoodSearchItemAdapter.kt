package com.example.fyp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R
import com.example.fyp.data.entities.FoodSearchResultList
import com.example.fyp.data.entities.MealItem

class FoodSearchItemAdapter : RecyclerView.Adapter<FoodSearchItemAdapter.ItemViewHolder>() {

    lateinit var onItemClickListener: OnItemClickListener
    var dataset: FoodSearchResultList? = null

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.list_item_name)
        val itemCalories: TextView = itemView.findViewById(R.id.list_item_calorie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.meal_item_list, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val results = dataset?.list
        results?.let {
            holder.itemName.text = it[position].itemName
            holder.itemCalories.text = it[position].itemCalories.toString()
            Log.e("bind", position.toString())

            holder.itemView.setOnClickListener {
                val mealItem = MealItem(results[position].itemName, results[position].itemCalories)
                onItemClickListener.addNewItem(mealItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataset?.list?.size ?: 0
    }
}

interface OnItemClickListener {
    fun addNewItem(item: MealItem)
}