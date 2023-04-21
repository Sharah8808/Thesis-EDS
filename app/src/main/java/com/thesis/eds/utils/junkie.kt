package com.thesis.eds.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import java.io.ByteArrayOutputStream

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


    private fun bundlingImage(filePath : String): Bundle {
        // Convert the saved image file to bitmap
        val bitmap = BitmapFactory.decodeFile(filePath)
        // Compress the bitmap to a stream and then convert the stream to a byte array
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()

        // Create a bundle to pass the byte array as an argument
        val bundle = Bundle().apply {
            putByteArray("imageData", byteArray)
        }
        return bundle
    }
}

//class CameraPreviewFragment : BottomSheetDialogFragment() {
//
//    private lateinit var binding: FragmentCameraPreviewBinding
//    private val args : CameraPreviewFragmentArgs by navArgs()
//    private lateinit var parentContext: Context
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentCameraPreviewBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val uri = args.uriArgs
//        Glide.with(this)
//            .load(uri)
//            .transform(RoundedCorners(20))
//            .into(binding.imgCameraPreview)
//
//        dialog?.setOnKeyListener { _, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
//                showExitAlertDialogCamView(parentContext)
//                true
//            } else {
//                false
//            }
//        }
//
//        binding.buttonNo.setOnClickListener{
//            val action = CameraPreviewFragmentDirections.actionCameraPreviewFragmentToNavDiagnostic()
//            findNavController().navigate(action)
//        }
//
//        binding.buttonYes.setOnClickListener{
//            val action = CameraPreviewFragmentDirections.actionCameraPreviewFragmentToDiagnosticResultFragment(uri)
//            findNavController().navigate(action)
//        }
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = BottomSheetDialog(requireContext(), R.style.AppTheme_BottomSheetDialog)
//        dialog.setCancelable(true)
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.setContentView(R.layout.fragment_camera_preview)
//
//        //Get the screen height
//        val screenHeight = resources.displayMetrics.heightPixels
//
//        // Set the peek height to a value that is tall enough to show all the views
//        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        dialog.setOnShowListener {
//            val bottomSheetDialog = it as BottomSheetDialog
//            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
//            bottomSheet?.let { it2 ->
//                val peekHeight = screenHeight - resources.getDimensionPixelSize(R.dimen.bottom_sheet_margin_bottom)
//                BottomSheetBehavior.from(it2).peekHeight = peekHeight
//            }
//        }
//        return dialog
//    }
//
//    private fun showExitAlertDialogCamView(context : Context) {
//        val alertDialogBuilder = AlertDialog.Builder(context)
//        alertDialogBuilder.setTitle("Confirmation")
//        alertDialogBuilder.setMessage("Kembali ke halaman sebelumnya?")
//            .setCancelable(false)
//            .setPositiveButton("Iya")  { _, _ ->
//                // Go back to the previous fragment and lose the current picture data
//                val action = CameraPreviewFragmentDirections.actionCameraPreviewFragmentToNavDiagnostic()
//                findNavController().navigate(action)
//            }
//            .setNegativeButton("Tidak") { dialog, _ ->
//                dialog.dismiss()
//            }
//
//        val alertDialog = alertDialogBuilder.create()
//        alertDialog.show()
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        parentContext = requireActivity()
//    }
//
//}
