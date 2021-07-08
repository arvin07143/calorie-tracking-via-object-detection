package com.example.fyp.barcodedetection

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R
import com.example.fyp.adapter.BarcodeFieldAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BarcodeResultsFragment : BottomSheetDialogFragment() {

    private val barcodeScanningViewModel: BarcodeScanningViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_barcode_results, container)
        val arguments = arguments

        val itemName =
            if (arguments?.containsKey(PRODUCT_NAME) == true) {
                arguments.getString(PRODUCT_NAME) ?: ""
            } else {
                Log.e(TAG, "No PROD NAME")
                ""
            }

        val itemCalories =
            if (arguments?.containsKey(PRODUCT_CALORIE) == true) {
                arguments.getInt(PRODUCT_CALORIE)
            } else {
                Log.e(TAG, "No PROD NAME")
                0
            }

        val itemPair = Pair(itemName, itemCalories)
        // Inflate the layout for this fragment
        view.findViewById<RecyclerView>(R.id.barcode_field_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = BarcodeFieldAdapter(listOf(itemPair))
        }
        return view
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        barcodeScanningViewModel.shouldScan.value = true
        super.onDismiss(dialogInterface)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param itemName Parameter 1.
         * @param itemCalories Parameter 2.
         * @return A new instance of fragment BarcodeResultsFragment.
         */
        private const val TAG = "BarcodeResultFragment"
        private const val PRODUCT_NAME = "arg_prod_name"
        private const val PRODUCT_CALORIE = "arg_prod_calorie"

        @JvmStatic
        fun show(
            itemName: String,
            itemCalories: Int,
            fragmentManager: FragmentManager,
        ) {
            val barcodeResultFragment = BarcodeResultsFragment()
            barcodeResultFragment.apply {
                arguments = Bundle().apply {
                    putString(PRODUCT_NAME, itemName)
                    putInt(PRODUCT_CALORIE, itemCalories)
                }
            }
            barcodeResultFragment.show(fragmentManager, TAG)
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as BarcodeResultsFragment?)?.dismiss()
        }

    }

}