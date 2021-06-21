package com.example.fyp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.fyp.barcodedetection.BarcodeScanningActivity
import com.example.fyp.databinding.AddMealFragmentBinding
import com.example.fyp.objectdetection.ObjectDetectionActivity

class AddMealFragmentDialog : DialogFragment() {

    private val args: AddMealFragmentDialogArgs by navArgs()

    companion object {
        fun newInstance() = AddMealFragmentDialog()
    }

    private val viewModel: AddMealViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.e("MENU", "SET")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar, menu)
        Log.e("MENU", "SET")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = AddMealFragmentBinding.inflate(layoutInflater)

        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    //TODO()
                    Log.e("ACTIVITY", it.data!!.getStringExtra("VAL")!!)
                }
            }

        binding.btnScanBarcode.setOnClickListener {
            val intent = Intent(requireActivity(), BarcodeScanningActivity::class.java)
            getContent.launch(intent)
        }

        binding.btnObjectDetection.setOnClickListener {
            val intent = Intent(requireActivity(), ObjectDetectionActivity::class.java)
            getContent.launch(intent)
        }

        return binding.root
    }


}