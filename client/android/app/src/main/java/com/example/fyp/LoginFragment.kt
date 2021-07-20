package com.example.fyp

import android.content.Intent
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
import com.example.fyp.databinding.FragmentLoginBinding
import com.example.fyp.utils.Utils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var webAPI: WebAPI

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            checkUserInformation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        binding.btnSignInWithEmail.setOnClickListener {
            val email = binding.emailField.editText?.text.toString()
            if (Utils.isValidEmail(email = email)) {
                auth.signInWithEmailAndPassword(
                    email,
                    binding.passwordField.editText?.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        checkUserInformation()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            this.context, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                binding.emailField.error = "Invalid Email"
            }
        }

        binding.txtRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_register)
        }

        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    checkUserInformation()
                    Log.d(TAG, "signInWithCredential:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }


    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun checkUserInformation() {
        if (!userPreviouslySignedIn()) {
            binding.progressCard.visibility = View.VISIBLE
            val call = webAPI.getUserInformation()
            call.enqueue(object : Callback<UserInformation> {
                override fun onResponse(
                    call: Call<UserInformation>,
                    response: retrofit2.Response<UserInformation>,
                ) {
                    if (response.isSuccessful) {
                        val userInformation = response.body()
                        if (userInformation != null) {
                            Utils.saveDataToSharedPreference(
                                sharedPreferences,
                                userInformation.uid!!,
                                userInformation.gender!!,
                                userInformation.height!!,
                                userInformation.weight!!,
                                userInformation.dateOfBirth!!
                            )
                            findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
                        }
                    }
                }

                override fun onFailure(call: Call<UserInformation>, t: Throwable) {
                    Log.e("CALL ERROR", t.message.toString())
                }
            })
        } else { //USER DATA ALREADY EXIST
            findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
        }
    }

    private fun userPreviouslySignedIn(): Boolean {
        return sharedPreferences.getBoolean("SIGNED IN", false)
    }

    companion object {
        private const val TAG = "LoginFragment"
        private const val RC_SIGN_IN = 9001
    }
}