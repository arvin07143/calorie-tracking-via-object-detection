package com.example.fyp

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fyp.data.entities.UserInformation
import com.example.fyp.data.remote.WebAPI
import com.example.fyp.databinding.FragmentRegisterBinding
import com.example.fyp.utils.Utils
import com.example.fyp.utils.Utils.saveDataToSharedPreference
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    @Inject
    lateinit var webAPI: WebAPI

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)

        binding.textDob.editText?.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker().setTitleText("Select Date").build()
            datePicker.show(parentFragmentManager, datePicker.toString())
            datePicker.addOnPositiveButtonClickListener {
                // Create calendar object and set the date to be that returned from selection
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.time = Date(it)
                val sdf = SimpleDateFormat.getDateInstance()
                binding.textDob.editText!!.setText(sdf.format(calendar.time))
            }
        }

        binding.heightPicker.minValue = 120
        binding.heightPicker.maxValue = 250
        binding.heightPicker.setFormatter {
            resources.getString(R.string.height_value, it)
        }

        binding.weightPicker.minValue = 30
        binding.weightPicker.maxValue = 200
        binding.heightPicker.setFormatter {
            resources.getString(R.string.height_value, it)
        }

        binding.btnRegisterFinish.setOnClickListener {
            register()
        }

        return binding.root
    }

    private fun register(): Boolean {
        val username = binding.registerProfileName.editText?.text.toString()
        val email = binding.textEmail.editText?.text.toString()
        val password = binding.textPassword.editText?.text.toString()
        val confirmPassword = binding.textConfirmPassword.editText?.text.toString()

        if (!Utils.isPasswordSame(password, confirmPassword)) {
            //validate

            return false
        }

        if (!Utils.isValidEmail(email)) {
            //validate

            return false
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                auth.currentUser?.updateProfile(userUpdate)
                saveProfileData()
            }
        }
        return true
    }


    private fun saveProfileData() {
        val uid = auth.currentUser!!.uid
        val height = binding.heightPicker.value
        val weight = binding.weightPicker.value
        val currentsdf = SimpleDateFormat.getDateInstance()
        val serversdf = SimpleDateFormat("yyyy-MM-dd")
        val dob = serversdf.format(currentsdf.parse(binding.textDob.editText?.text.toString()))
        val gender = when (binding.genderSelect.checkedButtonId) {
            R.id.btnMale -> 0
            R.id.btnFemale -> 1
            else -> 2
        }

        val userInformation =
            UserInformation(uid, gender, height, weight.toFloat(), Date(dob))

        saveDataToSharedPreference(sharedPreferences,
            uid,
            gender,
            height,
            weight.toFloat(),
            Date(dob))

        val registerCall = webAPI.registerNewUser(userInformation)

        registerCall.enqueue(object : Callback<JSONObject> {
            override fun onResponse(call: Call<JSONObject>, response: Response<JSONObject>) {
                Toast.makeText(requireContext(), "Registration Complete", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_register_to_mainActivity)
            }

            override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                Log.e("REGISTER", t.message.toString())
            }
        })
    }
}