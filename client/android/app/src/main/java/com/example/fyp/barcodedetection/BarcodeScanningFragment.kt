package com.example.fyp.barcodedetection

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.fyp.databinding.FragmentBarcodeScanningBinding
import com.google.mlkit.vision.barcode.Barcode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class BarcodeScanningFragment : Fragment(), BarcodeListener{

    private lateinit var binding: FragmentBarcodeScanningBinding
    private lateinit var barcodeScanner: MyBarcodeScanner
    private lateinit var barcodeAnalyzer: BarcodeAnalyzer
    private val viewModel: BarcodeScanningViewModel by activityViewModels()

    /** Blocking camera and inference operations are performed using this executor. */
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarcodeScanningBinding.inflate(inflater, container, false)
        barcodeAnalyzer = BarcodeAnalyzer(graphicOverlay = binding.overlay).apply {
            this.barcodeResultListener = this@BarcodeScanningFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newCachedThreadPool()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            barcodeScanner = MyBarcodeScanner.Builder(requireContext())
                .setLifecycleOwner(this)
                .setImageAnalyzer(barcodeAnalyzer)
                .build()

            barcodeScanner.addFutureListener(Runnable {
                barcodeScanner.setupCamera(requireActivity().windowManager, binding.viewfinder)
            }, ContextCompat.getMainExecutor(requireContext()))
        } else{
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        viewModel.shouldScan.observe(viewLifecycleOwner, Observer{
            if (it == true){
                barcodeScanner.startScanning()
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        barcodeScanner = MyBarcodeScanner.Builder(requireContext())
            .setLifecycleOwner(this)
            .setImageAnalyzer(barcodeAnalyzer)
            .build()

        barcodeScanner.addFutureListener(Runnable {
            barcodeScanner.setupCamera(requireActivity().windowManager, binding.viewfinder)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    companion object {
        fun newInstance() = BarcodeScanningFragment()

        // We only need to analyze the part of the image that has text, so we set crop percentages
        // to avoid analyze the entire image from the live camera feed.
        const val DESIRED_WIDTH_CROP_PERCENT = 8
        const val DESIRED_HEIGHT_CROP_PERCENT = 74

        // This is an arbitrary number we are using to keep tab of the permission
        // request. Where an app has multiple context for requesting permission,
        // this can help differentiate the different contexts
        private const val REQUEST_CODE_PERMISSIONS = 10

        // This is an array of all the permission specified in the manifest
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        private const val TAG = "MainFragment"
    }

    override fun onBarcodesDetected(barcodes: List<Barcode>) {
        for(barcode in barcodes){
            Log.e(TAG,barcode.rawValue ?: "")
        }
    }

    override fun onBarcodeProcessing() {
        barcodeScanner.stopPreview()
    }


    override fun onBarcodeProcessed(barcode: Barcode) {
        barcodeScanner.stopScanning()
        //TODO()
        viewModel.shouldScan.value = false
    }

}