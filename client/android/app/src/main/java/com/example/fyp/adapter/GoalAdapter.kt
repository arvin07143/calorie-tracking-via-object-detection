package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R
import com.example.fyp.data.entities.Goal

class GoalAdapter : RecyclerView.Adapter<GoalAdapter.ItemViewHolder>() {

    var dataset: List<Goal> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val goalName: TextView = itemView.findViewById(R.id.goal_type)
        val goalValue: TextView = itemView.findViewById(R.id.goal_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.goal_item, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val goalTypes = arrayOf("Weight", "Calorie")
        holder.goalName.text = goalTypes[dataset[position].goalType]

        when (dataset[position].goalType) {
            0 -> {
                holder.goalValue.text = holder.itemView.resources.getString(R.string.weight_value,
                    dataset[position].goalEndValue)
            }
            1 -> {
                holder.goalValue.text =
                    holder.itemView.resources.getString(R.string.calorie_value,
                        dataset[position].goalEndValue)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}