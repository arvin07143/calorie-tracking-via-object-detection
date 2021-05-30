package com.example.fyp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fyp.databinding.FragmentRegisterInformationBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.kevalpatel2106.rulerpicker.RulerValuePickerListener
import org.json.JSONObject

class RegisterInformationFragment : Fragment() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegisterInformationBinding.inflate(layoutInflater)

        val mUser = auth.currentUser
        var tokenCode = ""
        mUser?.getIdToken(true)?.addOnCompleteListener {
            tokenCode = if (it.isSuccessful) {
                it.result!!.token!!.toString()

            } else {
                ""
            }
        }

        binding.btnRegisterFinish.setOnClickListener {  }

        binding.heightPicker.selectValue(160)
        binding.weightPicker.selectValue(50)

        binding.textDob.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Date").build()
            datePicker.show(parentFragmentManager,datePicker.toString())
        }

        binding.heightPicker.setValuePickerListener(object : RulerValuePickerListener{
            override fun onValueChange(selectedValue: Int) {
            }

            override fun onIntermediateValueChange(selectedValue: Int) {
                binding.heightIndicator.text = getString(R.string.height_value,selectedValue)
            }

        })

        binding.weightPicker.setValuePickerListener(object : RulerValuePickerListener{
            override fun onValueChange(selectedValue: Int) {
            }

            override fun onIntermediateValueChange(selectedValue: Int) {
                binding.weightIndicator.text = getString(R.string.weight_value,selectedValue)
            }

        })

        binding.btnRegisterFinish.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
            val gender = binding.genderSelect.checkedButtonId
            val height = binding.heightPicker.currentValue
            val weight = binding.weightPicker.currentValue
            //val dob = binding.textDob.editText?.text.toString()
            val url = "http://10.0.2.2:5000/user/register/"
            val jsonBody = JSONObject()
            jsonBody.put("gender","MALE")
            jsonBody.put("weight",weight)
            jsonBody.put("height",height)
            //jsonBody.put("dob",dob)

            val registerRequest = object : JsonObjectRequest(Method.POST, url, jsonBody, {
                Toast.makeText(requireContext(),"Registration Complete",Toast.LENGTH_SHORT).show()
            },
                {
                    Log.e("REGISTER",it.toString())
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

            queue.add(registerRequest)

        }




        return binding.root
    }

    companion object {

    }
}