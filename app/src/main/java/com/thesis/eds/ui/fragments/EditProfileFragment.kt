package com.thesis.eds.ui.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.ActivityResultLauncher
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
import com.thesis.eds.interfaces.ActionBarTitleSetter
import com.thesis.eds.ui.viewModels.EditProfileViewModel
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException
import java.lang.Byte.decode
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment : Fragment() {

    private val viewModel by viewModels<EditProfileViewModel>()
    private var _binding : FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var selectImageLauncher: ActivityResultLauncher<String>

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
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val toolbar: Toolbar? = root.findViewById(R.id.toolbar)
        toolbar?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
        toolbar?.setNavigationOnClickListener { requireActivity().onBackPressed() }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadUserData()

//        showPhotoProfile()

        observeDataChanges()
//        updatePhotoProfile()

        clickListenerForTheCircleImage()
        clickListenerForTheSaveButton()
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

    private fun showPhotoProfile(){
        val imgView : CircleImageView = binding.imgAvatar
        val imgUri = viewModel.imageUri
        Glide.with(this)
            .load(imgUri)
            .into(imgView)
    }

    private fun updatePhotoProfile(){
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
//                        takePictureLauncher.launch(viewModel.createImageUri(requireContext()))
                        takePictureFromCamera()
                    }
                    options[item] == "Choose from Gallery" -> {
//                        selectImageLauncher.launch("image/*")
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


//    private fun showImagePickerDialog() {
//        val options = arrayOf("Take Picture", "Choose from Gallery", "Cancel")
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setItems(options) { _, which ->
//            when (which) {
//                0 -> takePicture.launch(viewModel.createImageUri(requireContext()))
//                1 -> choosePicture.launch("image/*")
//            }
//        }
//        builder.show()
//    }

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
//        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    viewModel.imageUri?.let { imageUri ->
                        uploadImageToFirebaseStorage(imageUri)
                    }
                }
                REQUEST_IMAGE_PICK -> {
//                    val imageUri = data?.data
//                    imageUri?.let {
//                        viewModel.imageUri = it
//                        binding.imgAvatar.setImageURI(it)
//                        uploadImageToFirebaseStorage(it)
//                    }
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
