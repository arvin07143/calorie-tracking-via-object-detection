package com.example.fyp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fyp.adapter.OnItemClickListener
import com.example.fyp.barcodedetection.BarcodeScanningActivity
import com.example.fyp.data.entities.Meal
import com.example.fyp.data.entities.MealItem
import com.example.fyp.databinding.AddMealFragmentBinding
import com.example.fyp.objectdetection.ObjectDetectionActivity
import com.example.fyp.viewmodels.AddMealViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMealFragmentDialog : DialogFragment(){

    private val args: AddMealFragmentDialogArgs by navArgs()
    private lateinit var binding: AddMealFragmentBinding

    companion object {
        fun newInstance() = AddMealFragmentDialog()
    }

    private val viewModel: AddMealViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = AddMealFragmentBinding.inflate(layoutInflater)

        viewModel.setMealType(args.mealType)

        viewModel.currentMealType.observe(viewLifecycleOwner,{ mealType ->
            viewModel.currentMeal = viewModel.getCurrentMeal(mealType)
            viewModel.currentMeal.observe(viewLifecycleOwner, {
                if (it == null) {
                    viewModel.addNewMeal()
                }
            })
        })



        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    //TODO()
                    Log.e("ACTIVITY", it.data!!.getStringExtra("VAL")!!)
                }
            }

        binding.foodSearchField.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchStringLiveData.value =
                    binding.foodSearchField.editText!!.text.toString()
                true
            } else {
                false
            }
        }

        val filePicker = registerForActivityResult(ActivityResultContracts.GetContent()) {
            val intent =
                Intent(requireActivity(), ObjectDetectionActivity::class.java)
            intent.putExtra("capture", false)
            intent.putExtra("content", it)
            getContent.launch(intent)
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
                        getContent.launch(intent)
                    }
                    R.id.camera_object_detect -> {
                        val intent = Intent(requireActivity(), ObjectDetectionActivity::class.java)
                        intent.putExtra("capture", false)
                        getContent.launch(intent)
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
        viewPager2.adapter = PagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "Search"
                else -> tab.text = "My Items"
            }
        }.attach()

    }

    private inner class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        private val mFragments = arrayOf(AddMealSearchFragment(), AddMealCustomItemFragment())

        override fun getItemCount(): Int = mFragments.size

        override fun createFragment(position: Int): Fragment {
            Log.e("ATTACH", "ATTACH $position")
            return mFragments[position]
        }

    }
}
