package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fyp.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    private lateinit var auth:FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        Log.e("a","FRAGMENT CREATE")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e("a","FRAGMENT CREATE")
        val binding = FragmentLoginBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        binding.btnSignInWithEmail.setOnClickListener {
            if (validateEmail(binding.emailField)) {
                auth.signInWithEmailAndPassword(
                    binding.emailField.editText?.text.toString(),
                    binding.passwordField.editText?.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(LoginFragment.TAG, "signInWithEmail:success")
                        findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(LoginFragment.TAG, "signInWithEmail:failure", it.exception)
                        Toast.makeText(
                            this.context, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == LoginFragment.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(LoginFragment.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(LoginFragment.TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(LoginFragment.TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(LoginFragment.TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }


    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, LoginFragment.RC_SIGN_IN)
    }

    private fun validateEmail(email: TextInputLayout): Boolean {
        val testEmail = email.editText?.text.toString()
        if (testEmail.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(testEmail).matches()) {
            return true
        } else {
            email.error = "Invalid Email"
            return false
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
        private const val RC_SIGN_IN = 9001
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
            }
    }
}