package com.example.fyp.barcodedetection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BarcodeScanningViewModel : ViewModel() {

    val shouldScan = MutableLiveData(false)
}