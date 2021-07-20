package com.example.fyp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fyp.barcodedetection.BarcodeScanningActivity
import com.example.fyp.data.entities.MealItem
import com.example.fyp.databinding.AddMealFragmentBinding
import com.example.fyp.objectdetection.DetectedObjectList
import com.example.fyp.objectdetection.ObjectDetectionActivity
import com.example.fyp.viewmodels.AddMealViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMealFragmentDialog : DialogFragment() {

    private val args: AddMealFragmentDialogArgs by navArgs()
    private lateinit var binding: AddMealFragmentBinding
    lateinit var listener: SearchListener

    companion object {
        fun newInstance() = AddMealFragmentDialog()
    }

    private val viewModel: AddMealViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = AddMealFragmentBinding.inflate(layoutInflater)

        viewModel.setMealType(args.mealType)

        viewModel.currentMealType.observe(viewLifecycleOwner, { mealType ->
            viewModel.currentMeal = viewModel.getCurrentMeal(mealType)
            viewModel.currentMeal.observe(viewLifecycleOwner, {
                if (it == null) {
                    viewModel.addNewMeal()
                }
            })
        })


        val getMealFromImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data =
                        it.data?.getParcelableExtra<DetectedObjectList>("detectedObjects")?.objectList
                    val newData = mutableListOf<MealItem>()
                    if (data != null) {
                        for (item in data) {
                            newData.add(MealItem(item.objectLabel, item.calories!!.toFloat()))
                        }
                    }
                    viewModel.addMealFromDetect(newData)
                }
            }

        val getMealFromBarcode =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {

                }
            }

        binding.foodSearchField.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                listener.search(binding.foodSearchField.editText!!.text.toString())
                false
            } else {
                false
            }
        }

        val filePicker = registerForActivityResult(ActivityResultContracts.GetContent()) {
            val intent =
                Intent(requireActivity(), ObjectDetectionActivity::class.java)
            intent.putExtra("capture", false)
            intent.putExtra("content", it)
            getMealFromImage.launch(intent)
        }

        binding.btnAddMealOverflow.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            val popUpInflater = popupMenu.menuInflater
            popUpInflater.inflate(R.menu.add_item_menu, popupMenu.menu)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.camera_barcode_scan -> {
                        val intent = Intent(requireActivity(), BarcodeScanningActivity::class.java)
                        getMealFromBarcode.launch(intent)
                    }
                    R.id.camera_object_detect -> {
                        val intent = Intent(requireActivity(), ObjectDetectionActivity::class.java)
                        intent.putExtra("capture", false)
                        getMealFromImage.launch(intent)
                    }
                    R.id.image_object_detect -> {
                        filePicker.launch("image/*")
                    }
                }
                true
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayout = binding.tabLayout
        val viewPager2 = binding.mealViewPager
        val adapter = PagerAdapter(this)
        listener = adapter.mFragments[0] as AddMealSearchFragment
        viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "Search"
                else -> tab.text = "My Items"
            }
        }.attach()

    }

    inner class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        val mFragments = arrayOf(AddMealSearchFragment(), AddMealCustomItemFragment() as Fragment)

        override fun getItemCount(): Int = mFragments.size

        override fun createFragment(position: Int): Fragment {
            Log.e("ATTACH", "ATTACH $position")
            return mFragments[position]
        }
    }

    interface SearchListener {
        fun search(searchTerm: String)
    }

}
