package com.example.fyp.objectdetection

import android.net.Uri
import androidx.camera.core.ImageProxy

interface CameraListener {

    fun onCaptureImage(uri: Uri)
}