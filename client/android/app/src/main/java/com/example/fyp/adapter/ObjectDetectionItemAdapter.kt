package com.example.fyp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R
import com.example.fyp.objectdetection.DetectedObjectList

internal class ObjectDetectionItemAdapter() :
    RecyclerView.Adapter<ObjectDetectionItemAdapter.ItemViewHolder>() {

    var detectedObjectList: DetectedObjectList? = null

    internal class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.detected_object_name)
        val itemCalories: TextView = view.findViewById(R.id.detected_object_calories)
        val btnEditItem: ImageButton = view.findViewById(R.id.btn_edit_object)
        val btnDeleteItem: ImageButton = view.findViewById(R.id.btn_delete_objet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.object_detection_item_list, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = detectedObjectList?.objectList?.get(position)
        if (item != null) {
            holder.itemName.text = item.objectLabel
            holder.itemCalories.text = (item.calories ?: 0).toString()
        }
        holder.btnDeleteItem.setOnClickListener {
            val itemList = detectedObjectList?.objectList?.toMutableList()
            itemList?.removeAt(position)
            if (detectedObjectList != null) {
                if (itemList != null) {
                    detectedObjectList!!.objectList = itemList
                }
            }
            notifyDataSetChanged()
            Log.i("DATASET", detectedObjectList?.objectList?.size.toString())
        }

        holder.btnEditItem.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return detectedObjectList?.objectList?.size ?: 0
    }
}