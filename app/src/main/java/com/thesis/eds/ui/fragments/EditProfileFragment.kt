package com.thesis.eds.ui.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.thesis.eds.BuildConfig
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentEditProfileBinding
import com.thesis.eds.ui.viewModels.EditProfileViewModel
import com.thesis.eds.utils.DialogUtils
import com.thesis.eds.utils.interfaces.ActionBarTitleSetter
import java.io.File

class EditProfileFragment : Fragment() {

    private val viewModel by viewModels<EditProfileViewModel>()
    private var _binding : FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            viewModel.imageUri?.let { imageUri ->
                uploadImageToFirebaseStorage(imageUri)
            }
        }
    }

    private val choosePicture = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.imageUri = it
            binding.imgAvatar.setImageURI(it)
            uploadImageToFirebaseStorage(it)
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
    }

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

        observeDataChanges()

        clickListenerForTheCircleImage()
        clickListenerForTheSaveButton()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                DialogUtils.showExitAlertDialog(requireContext()){
                    findNavController().navigateUp()
                }
            }
        }.apply { isEnabled = true }
        )
    }

    private fun observeDataChanges(){
        //Observe changes to the user's data in the ViewModel
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.etFullname.setText(user?.fullname)
            binding.etEmail.setText(user?.email)
            binding.etPhoneNumber.setText(user?.phoneNumber)
            val profilePictureUrl = user?.img

            if (profilePictureUrl != null) {
                Glide.with(this)
                    .load(profilePictureUrl)
                    .into(binding.imgAvatar)
            } else {
                // Use a default image if the user doesn't have a profile picture
                Glide.with(this)
                    .load(R.drawable.chawieputh)
                    .into(binding.imgAvatar)
            }
        }
    }

    private fun clickListenerForTheCircleImage(){
        // Set an OnClickListener to the CircleImageView
        binding.imgAvatar.setOnClickListener {
            // Show a dialog with options to take a picture or select an image from the gallery
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
                        takePictureFromCamera()
                    }
                    options[item] == "Choose from Gallery" -> {
                        choosePictureFromGallery()
                    }
                    options[item] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }
    }

    private fun clickListenerForTheSaveButton(){
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

    private fun takePictureFromCamera() {
        if (requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            val photoFile = createImageFile()
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                photoFile
            )
            viewModel.imageUri = photoURI
            takePicture.launch(photoURI)
        } else {
            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun choosePictureFromGallery() {
        choosePicture.launch("image/*")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    viewModel.imageUri?.let { imageUri ->
                        uploadImageToFirebaseStorage(imageUri)
                    }
                }
                REQUEST_IMAGE_PICK -> {
                    data?.let { choosePicture.launch(it.data.toString()) }
                }
            }
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = FirebaseStorage.getInstance().reference.child("images/$uid/profileImage.jpg")
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()
                viewModel.updateUserProfileImage(downloadUrl)
            } else {
                Toast.makeText(requireContext(), "Error uploading image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createImageFile(): File {
        val imageName = "${System.currentTimeMillis()}.jpg"
        val imageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageName, ".jpg", imageDir)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_edit_profil))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
