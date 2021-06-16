package com.example.fyp.barcodedetection

import com.google.mlkit.vision.barcode.Barcode

interface BarcodeListener {

    /**
     * Callback for when a barcode is found
     * @param barcode The barcode and its information that was found
     */
    fun onBarcodeProcessed(barcode:Barcode)

    /**
     * Callback for when a barcode is found and called before any additional processing of the barcode happens
     */
    fun onBarcodeProcessing()

    /**
     * Callback which gives a list of all detected barcodes the detector found in the image
     */
    fun onBarcodesDetected(barcodes:List<Barcode>)
}