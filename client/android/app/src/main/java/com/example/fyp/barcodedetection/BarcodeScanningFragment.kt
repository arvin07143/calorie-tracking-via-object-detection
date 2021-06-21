package com.example.fyp.barcodedetection

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
class BarcodeScanningFragment : Fragment(), BarcodeListener {

    private lateinit var binding: FragmentBarcodeScanningBinding
    private lateinit var barcodeScanner: MyBarcodeScanner
    private lateinit var barcodeAnalyzer: BarcodeAnalyzer
    private val viewModel: BarcodeScanningViewModel by activityViewModels()

    /** Blocking camera and inference operations are performed using this executor. */
    private lateinit var cameraExecutor: ExecutorService
    private var flashMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarcodeScanningBinding.inflate(inflater, container, false)
        barcodeAnalyzer = BarcodeAnalyzer(graphicOverlay = binding.barcodeScanningView.overlay).apply {
            this.barcodeResultListener = this@BarcodeScanningFragment
        }

        binding.barcodeScanningView.cameraTopMenu.closeButton.setOnClickListener {
            activity?.finish()
        }

        binding.barcodeScanningView.cameraTopMenu.flashButton.setOnClickListener {
            flashMode = !flashMode
            if (barcodeScanner.camera.cameraInfo.hasFlashUnit()) {
                barcodeScanner.camera.cameraControl.enableTorch(flashMode); // or false
            } else {
                Log.e("CAMERA", "NO FLASH")
                Toast.makeText(requireContext(), "No flash on this device", Toast.LENGTH_SHORT)
                    .show()
            }
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
                barcodeScanner.setupCamera(requireActivity().windowManager, binding.barcodeScanningView.viewfinder)
            }, ContextCompat.getMainExecutor(requireContext()))
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        viewModel.shouldScan.observe(viewLifecycleOwner, Observer {
            if (it == true) {
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
            barcodeScanner.setupCamera(requireActivity().windowManager, binding.barcodeScanningView.viewfinder)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    companion object {
        fun newInstance() = BarcodeScanningFragment()

        // This is an arbitrary number we are using to keep tab of the permission
        // request. Where an app has multiple context for requesting permission,
        // this can help differentiate the different contexts
        private const val REQUEST_CODE_PERMISSIONS = 10

        // This is an array of all the permission specified in the manifest
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val TAG = "MainFragment"
    }

    override fun onBarcodesDetected(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            Log.e(TAG, barcode.rawValue ?: "")
        }
    }

    override fun onBarcodeProcessing() {
        barcodeScanner.stopPreview()
    }


    override fun onBarcodeProcessed(barcode: Barcode) {
        barcodeScanner.stopScanning()
        val data = Intent()
        data.putExtra("VAL", barcode.rawValue)
        requireActivity().setResult(Activity.RESULT_OK, data)
        requireActivity().finish()
        viewModel.shouldScan.value = false
    }

}