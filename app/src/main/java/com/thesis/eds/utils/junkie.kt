package com.thesis.eds.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext

class junkie {

    //from EditProfileFragment, about the camera and gallery ---------------------------------------------------------------

//    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
//
//    private lateinit var photoUri: Uri
//
//    private fun takePicture() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        val photoFile: Uri = try {
//            createImageFile()
//        } catch (ex: IOException) {
//            // Error occurred while creating the File
//            null
//        }
//        photoFile?.also {
//            val photoURI: Uri = FileProvider.getUriForFile(
//                requireContext(),
//                "com.example.android.fileprovider",
//                it
//            )
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//            takePictureLauncher.launch(photoURI)
//        }
//    }
//
//    @Throws(IOException::class)
//    private fun createImageFile(): Uri {
//        // Create an image file name
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        ).apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
//        }.toUri()
//    }
//
//
//
//    private var currentPhotoPath: String? = null
//
//    init {
//        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val uri = Uri.parse(currentPhotoPath)
//                // Do something with the photo URI
//            }
//        }
//    }
//
//    //-----------------
//    private var pickImageLauncher: ActivityResultLauncher<String> =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//            // Do something with the selected image URI
//        }
//
//    private fun pickImage() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        pickImageLauncher.launch("image/*")
//    }

//    private fun selectImage() {
//        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setTitle("Add Photo!")
//        builder.setItems(options) { dialog, item ->
//            when {
//                options[item] == "Take Photo" -> {
//                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    startActivityForResult(takePicture, 0)
//                }
//                options[item] == "Choose from Gallery" -> {
//                    val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                    startActivityForResult(pickPhoto, 1)
//                }
//                options[item] == "Cancel" -> {
//                    dialog.dismiss()
//                }
//            }
//        }
//        builder.show()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            when (requestCode) {
//                0 -> {
//                    val selectedImage = data?.extras?.get("data") as Bitmap
//                    // Update the CircleImageView with the selected image
//                    binding.imgAvatar.setImageBitmap(selectedImage)
//                }
//                1 -> {
//                    val selectedImage = data?.data
//                    val filePath = arrayOf(MediaStore.Images.Media.DATA)
//                    val cursor = requireActivity().contentResolver.query(selectedImage!!, filePath, null, null, null)
//                    cursor?.moveToFirst()
//                    val columnIndex = cursor?.getColumnIndex(filePath[0])
//                    val picturePath = columnIndex?.let { cursor.getString(it) }
//                    cursor?.close()
//                    // Update the CircleImageView with the selected image
//                    binding.imgAvatar.setImageBitmap(BitmapFactory.decodeFile(picturePath))
//                }
//            }
//        }
//    }
}