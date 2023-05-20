package com.thesis.eds.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.base.CameraFragment
import com.jiangdg.ausbc.callback.ICameraStateCallBack
import com.jiangdg.ausbc.callback.ICaptureCallBack
import com.jiangdg.ausbc.utils.ToastUtils
import com.jiangdg.ausbc.widget.AspectRatioTextureView
import com.jiangdg.ausbc.widget.CaptureMediaView
import com.jiangdg.ausbc.widget.IAspectRatio
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentDiagnosticBinding
import com.thesis.eds.ui.viewModels.DiagnosticViewModel
import com.thesis.eds.utils.interfaces.ActionBarTitleSetter
import com.thesis.eds.utils.interfaces.MenuItemHighlighter
import java.io.ByteArrayOutputStream

class DiagnosticFragment : CameraFragment(), CaptureMediaView.OnViewClickListener {

    private var _binding: FragmentDiagnosticBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DiagnosticViewModel>()

    private val choosePictureGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val fileDescriptor = inputStream?.let { context?.contentResolver?.openFileDescriptor(uri, "r")?.fileDescriptor }
            val bitmapOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, bitmapOptions)

            // Calculate the dimensions according to model's input size
            val desiredWidth = 256
            val desiredHeight = 256
            val scaleFactor = minOf(
                bitmapOptions.outWidth / desiredWidth,
                bitmapOptions.outHeight / desiredHeight
            )

            // Set the desired options for decoding the bitmap with the calculated scale factor
            val bitmapDecodeOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = false
                inSampleSize = scaleFactor
            }

            // Decode the file descriptor into a resized bitmap
            val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, bitmapDecodeOptions)

            // Close the input stream and release the file descriptor
            inputStream?.close()

            val bitmapString = bitmapToString(bitmap)
            uri.toString()
            Log.d("EDSThesis_Diagnostic", "Variable check --->  Uri = $uri | Bitmap = $bitmap ----------")
            viewModel.savePhoto(requireActivity(), bitmap)
            viewModel.uri.observe(viewLifecycleOwner){itUri ->
                if (itUri != null){
                    navigateToDiagResultFragment(bitmapString, itUri)
                } else {
                    // handle null file path
                    Toast.makeText(requireActivity(), "Error saving picture.", Toast.LENGTH_SHORT).show()
                    Log.d("EDSThesis_Diagnostic", "Uri is failed to be made.")
                }
            }

            Toast.makeText(requireContext(), "Photo saved to: ${uri.path}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiagnosticBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        binding.buttonShot.setOnClickListener{
            captureImage()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                animateFlash()
            }
        }

        binding.buttonGallery.setOnClickListener{
            val currentFragmentId = navController.currentDestination?.id
            Log.d("EDSThesis_Diagnostic", currentFragmentId.toString() + " the current frag id ==========================================")
            choosePictureFromGallery()
        }
    }

    private fun choosePictureFromGallery() {
        choosePictureGallery.launch("image/*")
    }

    /**
     * Using com.github.jiangdongguo.AndroidUSBCamera:libausbc:3.3.3
     */

    override fun getCameraView(): IAspectRatio {
        return AspectRatioTextureView(requireContext())
    }

    override fun getCameraViewContainer(): ViewGroup {
        return binding.cameraPreview
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = FragmentDiagnosticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCameraState(self: MultiCameraClient.ICamera,
                               code: ICameraStateCallBack.State,
                               msg: String?) {
        when (code) {
            ICameraStateCallBack.State.OPENED -> handleCameraOpened()
            ICameraStateCallBack.State.CLOSED -> handleCameraClosed()
            ICameraStateCallBack.State.ERROR -> handleCameraError(msg)
        }
    }

    private fun handleCameraError(msg: String?) {
        ToastUtils.show("Camera opened error: $msg")
    }

    private fun handleCameraClosed() {
        ToastUtils.show("Camera closed success")
    }

    private fun handleCameraOpened() {
        ToastUtils.show("Camera opened success")
    }

    override fun getGravity(): Int = Gravity.CENTER

    override fun onViewClick(mode: CaptureMediaView.CaptureMode?) {
        TODO("Not yet implemented")
    }

    private fun captureImage() {
        captureImage(object : ICaptureCallBack {
            override fun onBegin() {
                Log.d("EDSThesis_Diagnostic", "Begin capturing image...")
            }

            override fun onError(error: String?) {
                ToastUtils.show(error ?: "Unknown exception.")
                Log.d("EDSThesis_Diagnostic", "Error capturing image !")

            }

            override fun onComplete(path: String?) {
                if (path != null) {
                    mediaScanner(path)
                }
                Log.d("EDSThesis_Diagnostic", "Complete capturing image. Path result = $path ----------")

                val capturedImageBitmap = BitmapFactory.decodeFile(path)
                val resizedImageBitmap = Bitmap.createScaledBitmap(capturedImageBitmap, 256, 256, true)
                val bitmapString = bitmapToString(resizedImageBitmap)
                viewModel.savePhoto(requireActivity(), resizedImageBitmap)
                viewModel.uri.observe(viewLifecycleOwner){itUri ->
                    if (itUri != null){
                        navigateToDiagResultFragment(bitmapString, itUri)
                    } else {
                        // handle null file path
                        Toast.makeText(requireActivity(), "Error saving picture.", Toast.LENGTH_SHORT).show()
                        Log.d("EDSThesis_Diagnostic", "No uri variable was being passed from capturing image method [Jiangdongguo].")
                    }
                }
                // Show success message
                Toast.makeText(requireContext(), "Photo saved to: $path", Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * - - ^_^
     */

    private fun navigateToDiagResultFragment(bitmap: String, uri : String){
        val action = DiagnosticFragmentDirections.actionNavDiagnosticToDiagnosticResultFragment(bitmap, uri)
        findNavController().navigate(action)
    }

    private fun mediaScanner(filePath: String){
        MediaScannerConnection.scanFile(
            requireContext(),
            arrayOf(filePath),
            null
        ) { path, _ -> Log.d("EDSThesis_Diagnostic", "Media scanned = $path ----------") }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun animateFlash() {
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        }, 100)
    }

    fun bitmapToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onAttach(context: Context) {
        Log.d("EDSThesis_Diagnostic", "Currently on Diagnostic Fragment...")
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_diagnosa))
        (activity as MenuItemHighlighter).setMenuHighlight(1)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /**
     * Below are previous codes, using back camera from CameraX library
     */

//    private val cameraProviderResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permissionGranted->
//        if(permissionGranted){
//            // cut and paste the previous startCamera() call here.
//            startCamera()
//        }else {
//            Snackbar.make(binding.root,"The camera permission is required", Snackbar.LENGTH_INDEFINITE).show()
//        }
//    }

//    private fun startCamera(){
//        // listening for data from the camera
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//            // connecting a preview use case to the preview in the xml file.
//            val preview = Preview.Builder().build().also{
//                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
//            }
//            imageCapture = ImageCapture.Builder().build()
//            try{
//                // clear all the previous use cases first.
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
//            } catch (e: Exception) {
//                Log.d(TAG, "Use case binding failed")
//            }
//
//        },ContextCompat.getMainExecutor(requireContext()))
//    }

//    private fun takePhoto(){
//        lifecycleScope.launch {
//            imageCapture?.let{ imageCapture ->
//                Create the output file with a unique name
//                val fileName = "JPEG_${System.currentTimeMillis()}.jpg"
//                val outputDirectory = viewModel.getOutputDirectory(requireActivity())
//                val outputFile = File(outputDirectory, fileName)
//
//                val lensFacing = CameraSelector.LENS_FACING_BACK // or CameraSelector.LENS_FACING_FRONT
//                val lensFacing = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
//                val metadata = ImageCapture.Metadata().apply {
//                    // Mirror image when using the front camera
////                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_BACK
//                }
//
//                // Create output options object which contains file + metadata
//                val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile)
//                    .setMetadata(metadata)
//                    .build()
//
//                // Take the picture and save to the output file
//                imageCapture.takePicture(
//                    outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
//                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                            // Image capture is successful, resize the captured image
//                            val capturedImageBitmap = BitmapFactory.decodeFile(outputFile.absolutePath)
//                            val resizedImageBitmap = Bitmap.createScaledBitmap(capturedImageBitmap, 256, 256, true)
//                            val bitmapString = bitmapToString(resizedImageBitmap)
//
//                            viewModel.savePhoto(requireContext(), requireActivity(), resizedImageBitmap)
//                            viewModel.uri.observe(viewLifecycleOwner){itUri ->
//                                if (itUri != null){
//                                    navigateToDiagResultFragment(bitmapString, itUri)
//                                } else {
//                                    // handle null file path
//                                    Toast.makeText(requireActivity(), "Error saving picture.", Toast.LENGTH_SHORT).show()
//                                    Log.d("EDSThesis_Diagnostic", "No uri variable was being passed from capturing image method.")
//                                }
//                            }
//                            // Show success message
//                            Toast.makeText(requireContext(), "Photo saved to: ${outputFile.absolutePath}", Toast.LENGTH_LONG).show()
//                        }
//                        override fun onError(exception: ImageCaptureException) {
//                            // Show error message
//                            Toast.makeText(requireContext(), "Error saving photo: ${exception.message}", Toast.LENGTH_LONG).show()
//                        }
//                    }
//                )
//            }
//        }
//    }

}