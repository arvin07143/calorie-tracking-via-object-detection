package com.example.fyp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R
import com.example.fyp.data.entities.MealItem
import com.example.fyp.data.entities.SavedItem

class SavedItemAdapter : RecyclerView.Adapter<SavedItemAdapter.ItemViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    var onItemUpdateClickListener: OnItemUpdateClickListener? = null
    var dataset: List<SavedItem> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

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

        dataset.let { list ->
            holder.itemName.text = list[position].foodName
            holder.itemCalories.text = list[position].calories.toString()
            holder.itemView.setOnClickListener {
                val mealItem = MealItem(dataset[position].foodName, dataset[position].calories)
                onItemClickListener?.onClick(mealItem)
                onItemUpdateClickListener?.onClick(savedItem = list[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}

interface OnItemUpdateClickListener{
    fun onClick(savedItem: SavedItem)
}