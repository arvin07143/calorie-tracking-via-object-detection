package com.example.fyp

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val selectedImage: ImageView = findViewById(R.id.image)
        val browseBtn: Button = findViewById(R.id.browseBtn)
        val uploadBtn: Button = findViewById(R.id.uploadBtn)
        val responseText: TextView = findViewById(R.id.responseText)

        val mUser = FirebaseAuth.getInstance().currentUser
        var tokenCode = ""
        mUser?.getIdToken(true)?.addOnCompleteListener {
            tokenCode = if (it.isSuccessful) {
                it.result!!.token!!.toString()

            } else {
                ""
            }
        }

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            selectedImage.setImageURI(it)
        }

        browseBtn.setOnClickListener {
            getContent.launch("image/*")
        }

        uploadBtn.setOnClickListener {
            val queue = Volley.newRequestQueue(this)
            val url = "http://10.0.2.2:5000/detection/predict/"
            val jsonBody = JSONObject()
            jsonBody.put("b64", imageUriToBase64())
            Log.e("JSON", jsonBody.toString())
            val stringRequest = object : JsonObjectRequest(Method.POST, url, jsonBody, {
                responseText.text = it.toString(2)
            },
                {
                    responseText.text = it.toString()
                    println(it)
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["Authorization"] = tokenCode
                    Log.e("AUTH",headers["Authorization"]!!)
                    return headers
                }
            }

            queue.add(stringRequest)
        }
    }

    private fun imageUriToBase64(): String {
        val selectedImage: ImageView = findViewById(R.id.image)
        val bitmap = (selectedImage.drawable as BitmapDrawable).bitmap
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray: ByteArray = outputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


}