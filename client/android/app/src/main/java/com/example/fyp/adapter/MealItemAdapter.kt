package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R
import com.example.fyp.data.entities.Meal

class MealItemAdapter : RecyclerView.Adapter<MealItemAdapter.ItemViewHolder>() {

    var dataset = arrayListOf<Meal>()

    class ItemViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
        val itemName:TextView = itemView.findViewById(R.id.list_item_name)
        val itemCalories:TextView = itemview.findViewById(R.id.list_item_calorie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.meal_item_list,parent,false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        item.mealContent.food_list.forEachIndexed{i,element ->
            holder.itemName.text = element
            holder.itemCalories.text = item.mealContent.calorie_list[i].toString()
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}