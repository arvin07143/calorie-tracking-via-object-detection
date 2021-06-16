package com.example.fyp.barcodedetection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp.R

class BarcodeScanningActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_barcode_scanning)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BarcodeScanningFragment.newInstance())
                .commitNow()
        }

    }

}