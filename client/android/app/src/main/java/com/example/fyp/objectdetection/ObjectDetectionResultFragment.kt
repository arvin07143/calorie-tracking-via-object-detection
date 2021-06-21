package com.example.fyp.objectdetection

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.fyp.R
import com.example.fyp.adapter.ObjectDetectionItemAdapter
import com.example.fyp.data.remote.WebAPI
import com.example.fyp.databinding.FragmentObjectDetectionResultBinding
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.security.auth.callback.Callback

@AndroidEntryPoint
class ObjectDetectionResultFragment : Fragment() {
    @Inject
    lateinit var webAPI:WebAPI
    private val args: ObjectDetectionResultFragmentArgs by navArgs()
    private lateinit var binding: FragmentObjectDetectionResultBinding
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUri = args.imageUri
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentObjectDetectionResultBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        binding.resultImageView.setImageURI(imageUri)

        webAPI.predictImage(imageUriToBase64(imageUri)).enqueue(object :retrofit2.Callback<DetectedObjectList>{
            override fun onResponse(
                call: Call<DetectedObjectList>,
                response: Response<DetectedObjectList>,
            ) {
                val adapter = ObjectDetectionItemAdapter(response.body()!!)
                binding.detectedObjectRecycler.adapter = adapter
            }

            override fun onFailure(call: Call<DetectedObjectList>, t: Throwable) {
                Log.e("RESULT ERROR",t.message.toString())
            }

        })

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @return A new instance of fragment ObjectDetectionResultFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ObjectDetectionResultFragment()
    }

    private fun imageUriToBase64(uri: Uri): String {
        try {
            val bytes = requireActivity().contentResolver.openInputStream(uri)?.readBytes()

            return Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e:IOException){
            Log.e("file",e.message.toString())
        }
        return ""
    }
}