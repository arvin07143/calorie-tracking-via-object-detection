package com.example.fyp.objectdetection

import android.net.Uri

interface CameraListener {

    fun onCaptureImage(uri: Uri)
}