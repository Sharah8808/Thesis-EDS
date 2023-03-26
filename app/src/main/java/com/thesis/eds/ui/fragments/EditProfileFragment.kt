package com.thesis.eds.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.thesis.eds.BuildConfig
import com.thesis.eds.databinding.FragmentEditProfileBinding
import com.thesis.eds.ui.viewModels.EditProfileViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment : Fragment() {

    private val viewModel by viewModels<EditProfileViewModel>()
    private var _binding : FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var selectImageLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadUserData()


//      Observe changes to the user's data in the ViewModel
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.etFullname.setText(user?.fullname)
            binding.etEmail.setText(user?.email)
            binding.etPhoneNumber.setText(user?.phoneNumber)
        }

        // Initialize the ActivityResultLaunchers
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                // Update the CircleImageView with the taken picture
                binding.imgAvatar.setImageURI(viewModel.imageUri)
            }
        }

        selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                // Update the CircleImageView with the selected image
                binding.imgAvatar.setImageURI(uri)
                viewModel.imageUri = uri
            }
        }

        // Set an OnClickListener to the CircleImageView
        binding.imgAvatar.setOnClickListener {
            // Show a dialog with options to take a picture or select an image from the gallery
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
                        takePictureLauncher.launch(viewModel.createImageUri(requireContext()))
                    }
                    options[item] == "Choose from Gallery" -> {
                        selectImageLauncher.launch("image/*")
                    }
                    options[item] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }

        // Save changes when the save button is clicked
        binding.buttonSave.setOnClickListener {
            val newName = binding.etFullname.text.toString()
            val newEmail = binding.etEmail.text.toString()
            val newPhoneNumber = binding.etPhoneNumber.text.toString()
            val newPassword = binding.etNewPassword.text.toString()
            val oldPassword = binding.etOldPassword.text.toString()

            viewModel.updateUserData(newName, newEmail, newPhoneNumber, newPassword, oldPassword)

            // Navigate back to the previous screen
            findNavController().popBackStack()
        }
    }




}
