package com.example.fyp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.fyp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.userProfileChangeRequest

class Register : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegisterBinding.inflate(layoutInflater)

        binding.btnNext.setOnClickListener {
            if (binding.textPassword.editText!!.text == binding.textConfirmPassword.editText!!.text) {
                auth.createUserWithEmailAndPassword(
                    binding.textEmail.editText!!.text.toString(),
                    binding.textPassword.editText!!.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userUpdates = userProfileChangeRequest {
                            displayName = binding.textProfileName.editText?.text.toString()
                        }
                        auth.currentUser!!.updateProfile(userUpdates).addOnCompleteListener {
                            if (it.isSuccessful) {
                                findNavController().navigate(R.id.action_register_to_registerInformationFragment)
                            }
                        }
                    } else {
                        try {
                            throw task.exception!!
                        } catch (ex: FirebaseAuthWeakPasswordException) {
                            Log.e("AUTH", "PASSWORD TOO WEAK")
                        } catch (ex: FirebaseAuthUserCollisionException) {
                            Log.e("AUTH", "USER EXIST")
                        } catch (ex: FirebaseAuthInvalidCredentialsException) {
                            Log.e("AUTH", "EMAIL IS MALFORMED")
                        }
                    }
                }
            }
        }

        return binding.root
    }
}