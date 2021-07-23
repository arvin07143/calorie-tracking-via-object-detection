package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R
import com.example.fyp.databinding.EditFoodDetailsDialogBinding
import com.example.fyp.objectdetection.DetectedObjectList
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ObjectDetectionItemAdapter :
    RecyclerView.Adapter<ObjectDetectionItemAdapter.ItemViewHolder>() {

    var detectedObjectList: DetectedObjectList? = null
    lateinit var onClickListener: View.OnClickListener

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.list_item_name)
        val itemCalories: TextView = view.findViewById(R.id.list_item_calorie)
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
        }

        holder.btnEditItem.setOnClickListener {
            val binding =
                EditFoodDetailsDialogBinding.inflate(LayoutInflater.from(holder.itemName.context))
            binding.foodNameField.editText?.setText(holder.itemName.text)
            binding.calorieValueField.editText?.setText(holder.itemCalories.text)
            val resources = holder.itemName.context.resources

            MaterialAlertDialogBuilder(holder.itemName.context)
                .setTitle("Edit Food")
                .setView(binding.root)
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.confirm)) { dialog, which ->
                    val itemList = detectedObjectList?.objectList?.toMutableList()

                    itemList?.get(position)?.let {
                        it.objectLabel = binding.foodNameField.editText?.text.toString()
                        it.calories = binding.calorieValueField.editText?.text.toString().toInt()
                    }
                    if (detectedObjectList != null) {
                        if (itemList != null) {
                            detectedObjectList!!.objectList = itemList
                        }
                    }
                    notifyDataSetChanged()
                }
                .show()
        }

    }

    override fun getItemCount(): Int {
        return detectedObjectList?.objectList?.size ?: 0
    }
}