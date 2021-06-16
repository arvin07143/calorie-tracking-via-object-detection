package com.example.fyp.barcodedetection

import android.animation.ValueAnimator
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.fyp.barcodedetection.graphics.BarcodeLoadingGraphic
import com.example.fyp.barcodedetection.graphics.BarcodeReticleGraphic
import com.example.fyp.camera.CameraReticleAnimator
import com.example.fyp.camera.GraphicOverlay
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

class BarcodeAnalyzer(
    private val graphicOverlay: GraphicOverlay
) : ImageAnalysis.Analyzer {
    private val cameraReticleAnimator: CameraReticleAnimator = CameraReticleAnimator(graphicOverlay)
    private val barcodeScanner = BarcodeScanning.getClient()
    private val _isRunning: AtomicBoolean = AtomicBoolean(false)
    private lateinit var barcodeReticle: BarcodeReticleGraphic

    var barcodeResultListener: BarcodeListener? = null
    private var shouldShowLoadingAnimation: Boolean = true

    @ExperimentalGetImage
    override fun analyze(image: ImageProxy) {

        if (_isRunning.get()) {
            image.close()
            return
        }

        val mediaImage = image.image ?: return

        detectBarcode(InputImage.fromMediaImage(mediaImage, 0)).addOnCompleteListener {
            image.close()
        }
        graphicOverlay.invalidate()
    }

    private fun detectBarcode(image: InputImage): Task<MutableList<Barcode>> {
        return barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                graphicOverlay.clear()
                if (barcodes.size == 0) {
                    cameraReticleAnimator.start()
                    barcodeReticle = BarcodeReticleGraphic(graphicOverlay, cameraReticleAnimator)
                    graphicOverlay.add(barcodeReticle)
                } else {
                    barcodeResultListener?.onBarcodesDetected(barcodes)
                    _isRunning.set(true)
                    cameraReticleAnimator.cancel()

                    barcodeResultListener?.onBarcodeProcessing()

                    if (shouldShowLoadingAnimation) {
                        val loadingAnimator =
                            createLoadingAnimator(graphicOverlay, barcodes.first())
                        loadingAnimator.start()
                        graphicOverlay.add(BarcodeLoadingGraphic(graphicOverlay, loadingAnimator))
                    } else {
                        barcodeResultListener?.onBarcodeProcessed(barcodes.first())
                        barcodeReticle =
                            BarcodeReticleGraphic(graphicOverlay, cameraReticleAnimator)
                        graphicOverlay.add(barcodeReticle)
                        _isRunning.set(false)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("BARCODE", "Barcode detection failure", exception)
            }
    }

    private fun createLoadingAnimator(
        graphicOverlay: GraphicOverlay,
        barcode: Barcode
    ): ValueAnimator {
        val endProgress = 1.1f
        return ValueAnimator.ofFloat(0f, endProgress).apply {
            duration = 2000
            addUpdateListener {
                if ((animatedValue as Float).compareTo(endProgress) >= 0) {
                    graphicOverlay.clear()
                    barcodeResultListener?.onBarcodeProcessed(barcode)
                    _isRunning.set(false)
                } else {
                    graphicOverlay.invalidate()
                }
            }
        }
    }
}
