package com.example.fyp

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.fyp.databinding.EditPasswordDialogBinding
import com.example.fyp.databinding.EditProfileNameBinding
import com.example.fyp.databinding.FragmentSettingsBinding
import com.example.fyp.databinding.PickerDialogBinding
import com.example.fyp.utils.Utils
import com.example.fyp.viewmodels.SettingViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        binding.btnLogOut.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Log Out ?")
                .setMessage("Are you sure that you want to log out ?")
                .setPositiveButton("Confirm") { _, _ ->
                    auth.signOut()
                    viewModel.logOut()
                    findNavController().navigate(R.id.action_settingsFragment_to_loginActivity)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.btnEditProfileName.setOnClickListener {
            val binding = EditProfileNameBinding.inflate(layoutInflater)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Profile Name")
                .setView(binding.root)
                .setPositiveButton("Confirm") { _, _ ->
                    val newName = binding.profileNameField.editText?.text.toString()
                    auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder()
                        .setDisplayName(newName).build())
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        val filePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uploadImage(uri, null)
        }

        binding.btnChangeHeight.setOnClickListener {
            val binding = PickerDialogBinding.inflate(layoutInflater)
            binding.numberPicker.minValue = 120
            binding.numberPicker.maxValue = 250
            viewModel.getLiveSharedPreference().getInt("height",160).observe(viewLifecycleOwner){
                binding.numberPicker.value = it
            }
            binding.numberPicker.setFormatter {
                resources.getString(R.string.height_value, it)
            }
            binding.numberPicker.wrapSelectorWheel = false

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Height")
                .setView(binding.root)
                .setPositiveButton("Confirm") { dialog, _ ->
                    viewModel.updateUserInformation(height = binding.numberPicker.value,
                        gender = null,
                        weight = null,
                        dateOfBirth = null)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.btnChangeWeight.setOnClickListener {
            val binding = PickerDialogBinding.inflate(layoutInflater)
            binding.numberPicker.minValue = 30
            binding.numberPicker.maxValue = 200
            viewModel.getLiveSharedPreference().getFloat("weight",60F).observe(viewLifecycleOwner){
                binding.numberPicker.value = it.toInt()
            }
            binding.numberPicker.setFormatter {
                resources.getString(R.string.weight_value, it)
            }
            binding.numberPicker.wrapSelectorWheel = false

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Weight")
                .setView(binding.root)
                .setPositiveButton("Confirm") { dialog, _ ->
                    viewModel.updateUserInformation(weight = binding.numberPicker.value.toFloat(),
                        gender = null,
                        height = null,
                        dateOfBirth = null)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.btnChangeProfileImage.setOnClickListener {
            val items = arrayOf("Take Picture", "Select From Device", "Remove Picture")
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Profile Picture")
                .setItems(items) { dialog, which ->
                    when (which) {
                        0 -> dispatchTakePictureIntent()
                        1 -> filePicker.launch("image/*")
                        2 -> {
                            auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder()
                                .setPhotoUri(null).build())
                        }
                        else -> dialog.dismiss()
                    }
                }
                .show()
        }

        binding.btnDeleteAccount.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure that you want to delete your account? All data will be removed.")
                .setPositiveButton("Confirm") { dialog, which ->
                    auth.currentUser?.delete()
                    findNavController().navigate(R.id.action_settingsFragment_to_loginActivity)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.btnChangeGender.setOnClickListener {
            val genders = arrayOf("Male", "Female", "Others")
            var selected = 0
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Set Gender")
                .setSingleChoiceItems(genders, 0) { _, which ->
                    selected = which
                }
                .setPositiveButton("Confirm") { _, _ ->
                    viewModel.updateUserInformation(weight = null,
                        gender = selected,
                        height = null,
                        dateOfBirth = null)
                }
                .show()
        }

        binding.btnChangeDob.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())
            val datePicker = datePicker()
                .setTitleText("Set Date of Birth")
                .setCalendarConstraints(constraintsBuilder.build())
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build()

            datePicker.addOnPositiveButtonClickListener {
                viewModel.updateUserInformation(weight = null,
                    gender = null,
                    height = null,
                    dateOfBirth = Date(it))
            }
            datePicker.show(requireActivity().supportFragmentManager,"tag")
        }


        binding.btnChangePassword.setOnClickListener {
            val binding = EditPasswordDialogBinding.inflate(layoutInflater)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Password")
                .setView(binding.root)
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Confirm", null)
                .create().apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            val currentPassword =
                                binding.currentPasswordField.editText?.text.toString()
                            val newPassword = binding.newPasswordField.editText?.text.toString()
                            val confirmPassword =
                                binding.confirmPasswordField.editText?.text.toString()
                            if (Utils.isPasswordSame(newPassword, confirmPassword)) {
                                binding.confirmPasswordField.error = null
                                val authCredential =
                                    EmailAuthProvider.getCredential(auth.currentUser?.email.toString(),
                                        currentPassword)
                                auth.currentUser?.reauthenticate(authCredential)
                                    ?.addOnCompleteListener { reauth ->
                                        if (reauth.isSuccessful) {
                                            auth.currentUser!!.updatePassword(newPassword)
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        Toast.makeText(requireContext(),
                                                            "Password Changed Successfully",
                                                            Toast.LENGTH_LONG).show()
                                                        dismiss()
                                                    }
                                                }
                                                .addOnFailureListener {
                                                    if (it is FirebaseAuthWeakPasswordException) {
                                                        binding.newPasswordField.error = it.reason
                                                    }
                                                }
                                        }
                                    }
                                    ?.addOnFailureListener {
                                        Log.e("CHANGE PASSWORD", it.message.toString())
                                        binding.currentPasswordField.error = "Incorrect password"
                                    }
                            } else {
                                binding.confirmPasswordField.error = "Password does not match"
                            }
                        }
                    }
                }.show()
        }

        return binding.root
    }

    private val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val stream = ByteArrayOutputStream()
            imageBitmap.compress(CompressFormat.JPEG, 100, stream)
            val inputStream: InputStream = ByteArrayInputStream(stream.toByteArray())
            uploadImage(null, inputStream)
        }
    }

    private fun uploadImage(uri: Uri?, inputStream: InputStream?) {
        val storageRef = storage.reference
        val fileInputStream: InputStream? = if (uri != null) {
            requireContext().contentResolver.openInputStream(uri)
        } else {
            inputStream
        }
        val profileImageRef = storageRef.child("images/${auth.currentUser?.uid}.jpg")
        val uploadTask = fileInputStream?.let { it1 -> profileImageRef.putStream(it1) }
        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            profileImageRef.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUri).build())
            } else {
                Log.e("FILE UPLOAD", task.exception?.message.toString())
            }
        }
    }

}