package com.example.fyp.objectdetection

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fyp.R
import com.example.fyp.adapter.ObjectDetectionItemAdapter
import com.example.fyp.data.remote.WebAPI
import com.example.fyp.databinding.FragmentObjectDetectionResultBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject


@AndroidEntryPoint
class ObjectDetectionResultFragment : Fragment() {
    @Inject
    lateinit var webAPI: WebAPI
    private val args: ObjectDetectionResultFragmentArgs by navArgs()
    private lateinit var binding: FragmentObjectDetectionResultBinding
    private lateinit var imageUri: Uri
    private var isFile = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUri = args.imageUri
        isFile = args.isFile
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentObjectDetectionResultBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        binding.resultImageView.setImageURI(imageUri)

        val adapter = ObjectDetectionItemAdapter()
        binding.detectedObjectRecycler.adapter = adapter

        val file = requireContext().contentResolver.openInputStream(imageUri)

        file?.let {
            val part = MultipartBody.Part.createFormData("file",
                "image",
                RequestBody.create(MediaType.parse("image/*"), it.readBytes()))

            webAPI.predictImage(part)
                .enqueue(object : retrofit2.Callback<DetectedObjectList> {
                    override fun onResponse(
                        call: Call<DetectedObjectList>,
                        response: Response<DetectedObjectList>,
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            adapter.detectedObjectList = response.body()
                            Log.i("DETECTED", response.body()!!.objectList.size.toString())
                            adapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(call: Call<DetectedObjectList>, t: Throwable) {
                        Log.e("RESULT ERROR", t.message.toString())
                    }
                })
        }

        binding.objectDetectionConfirm.setOnClickListener {
            val data = adapter.detectedObjectList
            val resultIntent = Intent()
            if (data != null) {
                resultIntent.putExtra("detectedObjects", data)
            }
            requireActivity().setResult(Activity.RESULT_OK, resultIntent)
            requireActivity().finish()
        }

        binding.objectDetectionCancel.setOnClickListener {
            if (!isFile){
                findNavController().navigate(R.id.action_objectDetectionResultFragment_to_objectDetectionFragment)
            } else {
                requireActivity().finish()
            }
        }

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

}