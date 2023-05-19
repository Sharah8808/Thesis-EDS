package com.thesis.eds.ui.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.camera2.CameraManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentDiagnosticBinding
import com.thesis.eds.ui.viewModels.DiagnosticViewModel
import com.thesis.eds.utils.interfaces.ActionBarTitleSetter
import com.thesis.eds.utils.interfaces.MenuItemHighlighter
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class DiagnosticFragment : Fragment() {

    private var _binding: FragmentDiagnosticBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DiagnosticViewModel>()
    private var imageCapture: ImageCapture? = null
    private lateinit var imgCaptureExecutor: ExecutorService
//    private var uriResult : Uri? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var onBackPressedCallback: OnBackPressedCallback? = null

    private val choosePictureGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val fileDescriptor = inputStream?.let { context?.contentResolver?.openFileDescriptor(uri, "r")?.fileDescriptor }
            val bitmapOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, bitmapOptions)

// Calculate the desired dimensions for the resized bitmap
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

// Pass the bitmap to the savePhoto function


// Close the input stream and release the file descriptor
            inputStream?.close()
//            fileDescriptor?.close()

// Pass the bitmap to the savePhoto function
//            viewModel.savePhoto(uri, requireContext(), requireActivity(), bitmap)

            val bitmapString = bitmapToString(bitmap)

            Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!! are these null? uri = $uri | bitmap = $bitmap ---------------------------------------??")
            viewModel.savePhoto(uri, requireContext(), requireActivity(), bitmap)
            viewModel.outputFilePath.observe(viewLifecycleOwner) { filePath ->
                if (filePath != null) {
                    mediaScanner(filePath)
//                    navigateToDiagResultFragment(uri.toString())
                    navigateToDiagResultFragment(bitmapString)
                } else {
                    // handle null file path
                    Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!! Well bye byeee no filepath sadge")
                }
            }
            Toast.makeText(requireContext(), "Photo saved to: ${uri.path}", Toast.LENGTH_LONG).show()
        }
    }

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector

    //-----------new ones
    private lateinit var camera : Camera
    private lateinit var cameraManager: CameraManager
//    private lateinit var cameraDevice: CameraDevice
//    private lateinit var cameraCaptureSession: CameraCaptureSession
//    private lateinit var imageReader: ImageReader
//    private lateinit var cameraCharacteristics: CameraCharacteristics
//    private lateinit var ORIENTATIONS: SparseIntArray
//
//    private val cameraStateCallback = object : CameraDevice.StateCallback() {
//        override fun onOpened(camera: CameraDevice) {
//            cameraDevice = camera
//            createCaptureSession()
//        }
//
//        override fun onDisconnected(camera: CameraDevice) {
//            cameraDevice.close()
//        }
//
//        override fun onError(camera: CameraDevice, error: Int) {
//            cameraDevice.close()
//        }
//    }
//
//    private val captureSessionStateCallback = object : CameraCaptureSession.StateCallback() {
//        override fun onConfigured(session: CameraCaptureSession) {
//            cameraCaptureSession = session
//            startPreview()
//        }
//
//        override fun onConfigureFailed(session: CameraCaptureSession) {
//            // Handle configuration failure
//        }
//    }


    //----------------------


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

        //old code dont delete ------------------
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        imgCaptureExecutor = Executors.newSingleThreadExecutor()
        //-------------------------

//        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//        cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_EXTERNAL).build()
//        cameraProviderResult.launch(android.Manifest.permission.CAMERA)
//        imgCaptureExecutor = Executors.newSingleThreadExecutor()


//        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//        cameraProviderResult.launch(android.Manifest.permission.CAMERA)
//        imgCaptureExecutor = Executors.newSingleThreadExecutor()

//        cameraProviderFuture.addListener(Runnable {
//            // Camera provider is now guaranteed to be available
//            val cameraProvider = cameraProviderFuture.get()
//
//            // Set up the preview use case to display camera preview.
//            val preview = Preview.Builder().build()
//
//            // Set up the capture use case to allow users to take photos.
//            imageCapture = ImageCapture.Builder()
//                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//                .build()
//
//            // Choose the camera by requiring a lens facing
//            val cameraSelector = CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build()
//
//            // Attach use cases to the camera with the same lifecycle owner
//            val camera = cameraProvider.bindToLifecycle(
//                this as LifecycleOwner, cameraSelector, preview, imageCapture)
//
//            // Connect the preview use case to the previewView
//            preview.setSurfaceProvider(
//                binding.cameraPreview.surfaceProvider
//            )
//        }, ContextCompat.getMainExecutor(requireContext()))
//        getExternalCameraId()


//        camera =
                //-------------------------

//        cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
//
//        initializeCameraCharacteristics()
//
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            openCamera()
//        } else {
//            Snackbar.make(binding.root, "The camera permission is required", Snackbar.LENGTH_INDEFINITE)
//                .show()
//        }


        //--------------------


        binding.buttonShot.setOnClickListener{
//            Log.d("Current Fragment ID", currentFragmentId.toString() + " the current frag id ==========================================")
            takePhoto()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                animateFlash()
            }
        }

        binding.buttonGallery.setOnClickListener{
            val currentFragmentId = navController.currentDestination?.id
            Log.d("Current Fragment ID", currentFragmentId.toString() + " the current frag id ==========================================")
            choosePictureFromGallery()
        }

    }

//    private fun openCamera() {
//        try {
//            val cameraId = getExternalCameraId() // Modify this method to obtain the external camera ID
//            if (ActivityCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.CAMERA
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return
//            }
//            cameraManager.openCamera(cameraId, cameraStateCallback, null)
//        } catch (e: CameraAccessException) {
//            // Handle camera access exception
//        }
//    }
//
//    private fun getExternalCameraId(): String {
//        val cameraIds = cameraManager.cameraIdList
//        for (id in cameraIds) {
//            val characteristics = cameraManager.getCameraCharacteristics(id)
//            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
//            if (lensFacing == CameraCharacteristics.LENS_FACING_EXTERNAL) {
//                return id
//            }
//        }
//        Toast.makeText(requireActivity(), "No camera found?? $cameraIds", Toast.LENGTH_SHORT).show()
//        // Handle case when external camera not found
//        throw RuntimeException("External camera not found")
//    }
//
//    private fun createCaptureSession() {
//        val surfaces = listOf(
//            binding.cameraPreview.holder.surface,
//            imageReader.surface
//        )
//
//        try {
//            cameraDevice.createCaptureSession(surfaces, captureSessionStateCallback, null)
//        } catch (e: CameraAccessException) {
//            // Handle camera access exception
//        }
//    }
//
//
//    private fun startPreview() {
//        val previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
//        previewRequestBuilder.addTarget(binding.cameraPreview.holder.surface)
//
//        try {
//            cameraCaptureSession.setRepeatingRequest(
//                previewRequestBuilder.build(),
//                null,
//                null
//            )
//        } catch (e: CameraAccessException) {
//            // Handle camera access exception
//        }
//    }
//
//    private fun takePhoto() {
//        try {
//            val lensFacing = CameraCharacteristics.LENS_FACING_BACK // or CameraCharacteristics.LENS_FACING_FRONT
//
//            // Setup image capture metadata
//            val metadata = ImageCapture.Metadata().apply {
//                // Mirror image when using the front camera
//                isReversedHorizontal = lensFacing == CameraCharacteristics.LENS_FACING_BACK
//            }
//
//            // Create the output file with a unique name
//            val fileName = "JPEG_${System.currentTimeMillis()}.jpg"
//            val outputDirectory = viewModel.getOutputDirectory(requireActivity())
//            val outputFile = File(outputDirectory, fileName)
//
//            // Create the capture request
//            val captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
//            captureBuilder.addTarget(imageReader.surface)
//            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
//            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation())
//
//            // Set image capture metadata
//            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation())
//            captureBuilder.set(CaptureRequest.JPEG_QUALITY, 90)
//            captureBuilder.set(CaptureRequest.JPEG_THUMBNAIL_SIZE, Size(512, 384))
//            captureBuilder.set(CaptureRequest.JPEG_THUMBNAIL_QUALITY, 90)
//            captureBuilder.set(CaptureRequest.JPEG_GPS_LOCATION, Location("")) // Set GPS location if needed
//            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation())
//            captureBuilder.set(CaptureRequest.JPEG_GPS_LOCATION, null) // Clear GPS location
//
//            // Create the capture session capture callback
//            val captureCallback = object : CameraCaptureSession.CaptureCallback() {
//                override fun onCaptureCompleted(
//                    session: CameraCaptureSession,
//                    request: CaptureRequest,
//                    result: TotalCaptureResult
//                ) {
//                    // Image capture completed
//                }
//
//                override fun onCaptureFailed(
//                    session: CameraCaptureSession,
//                    request: CaptureRequest,
//                    failure: CaptureFailure
//                ) {
//                    // Image capture failed
//                }
//            }
//
//            // Start the image capture
//            cameraCaptureSession.capture(
//                captureBuilder.build(),
//                captureCallback,
//                null
//            )
//        } catch (e: CameraAccessException) {
//            // Handle camera access exception
//        }
//    }
//
//    private fun getJpegOrientation(): Int {
//        val deviceOrientation = requireActivity().windowManager.defaultDisplay.rotation
//        val sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
//        return (ORIENTATIONS.get(deviceOrientation) + sensorOrientation!! + 270) % 360
//    }
//
//    private fun initializeCameraCharacteristics() {
//        val cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
//        val externalCameraId = findExternalCameraId(cameraManager)
//
//        if (externalCameraId != null) {
//            cameraCharacteristics = cameraManager.getCameraCharacteristics(externalCameraId)
//            ORIENTATIONS = SparseIntArray()
//            ORIENTATIONS.append(Surface.ROTATION_0, 90)
//            ORIENTATIONS.append(Surface.ROTATION_90, 0)
//            ORIENTATIONS.append(Surface.ROTATION_180, 270)
//            ORIENTATIONS.append(Surface.ROTATION_270, 180)
//        } else {
//            // Handle the absence of an external camera or other error condition
//        }
//    }
//
//    private fun findExternalCameraId(cameraManager: CameraManager): String? {
//        val cameraIds = cameraManager.cameraIdList
//        for (id in cameraIds) {
//            val characteristics = cameraManager.getCameraCharacteristics(id)
//            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
//            if (facing == CameraCharacteristics.LENS_FACING_EXTERNAL) {
//                return id
//            }
//        }
//        return null
//    }

//    @SuppressLint("UnsafeOptInUsageError")
//    private fun getExternalCameraId(): String {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//        val cameraProvider = cameraProviderFuture.get()
//        val preview = Preview.Builder().build()
//        val cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(CameraSelector.LENS_FACING_EXTERNAL)
//            .build()
//
//        try {
//            val camera = cameraProvider.bindToLifecycle(
//                this as LifecycleOwner, cameraSelector, preview, imageCapture)
////            val cameraProvider = cameraProviderFuture.get()
////            val cameraInfo = cameraProvider.getCameraInfo(cameraSelector)
//            val cameraId = camera.cameraInfo.toString()
//
//            Log.d(TAG, "External Camera Found: $cameraId")
//            return cameraId
//        } catch (e: ExecutionException) {
//            Log.e(TAG, "Failed to retrieve camera provider", e)
//        } catch (e: InterruptedException) {
//            Log.e(TAG, "Interrupted while retrieving camera provider", e)
//        }
//
//        Toast.makeText(requireActivity(), "No external camera found", Toast.LENGTH_SHORT).show()
//        throw RuntimeException("External camera not found")
//    }




    //------------------------------- above are new
    private fun choosePictureFromGallery() {
        choosePictureGallery.launch("image/*")
    }

//    private fun startCamera(){
//        // listening for data from the camera
//        cameraProviderFuture.addListener({
//            Toast.makeText(requireActivity(), "is camera started?", Toast.LENGTH_SHORT).show()
//            val cameraProvider = cameraProviderFuture.get()
//
//            // create a preview use case
//            val preview = Preview.Builder().build().also{
//                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
//            }
//            // create an image capture use case
//            imageCapture = ImageCapture.Builder().build()
//
//            try{
//                // clear all the previous use cases first.
//                cameraProvider.unbindAll()
//                // add the use cases to the cameraProvider
//                val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
//            } catch (e: Exception) {
//                Log.d(TAG, "Use case binding failed")
//            }
//
//        }, ContextCompat.getMainExecutor(requireContext()))
//    }

//

    private fun navigateToDiagResultFragment(uri : String){
        val action = DiagnosticFragmentDirections.actionNavDiagnosticToDiagnosticResultFragment(uri)
        findNavController().navigate(action)
    }

    private fun mediaScanner(filePath: String){
        MediaScannerConnection.scanFile(
            requireContext(),
            arrayOf(filePath),
            null,
            object : MediaScannerConnection.OnScanCompletedListener {
                override fun onScanCompleted(path: String?, uri: Uri?) {
                    Log.d(TAG, "Scanned $path")
                }
            }
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        Toast.makeText(requireActivity(), "is kierooo heerre?", Toast.LENGTH_SHORT).show()
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_diagnosa))
        (activity as MenuItemHighlighter).setMenuHighlight(1)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // regular camera, DO NOT DELETE :"
    private fun startCamera(){
        // listening for data from the camera
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            // connecting a preview use case to the preview in the xml file.
            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            try{
                // clear all the previous use cases first.
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
            } catch (e: Exception) {
                Log.d(TAG, "Use case binding failed")
            }

        },ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun takePhoto(){
        lifecycleScope.launch {
            imageCapture?.let{ imageCapture ->
                Toast.makeText(requireActivity(), "is imagecapture capturing?", Toast.LENGTH_SHORT).show()
                // Create the output file with a unique name
                val fileName = "JPEG_${System.currentTimeMillis()}.jpg"
                val outputDirectory = viewModel.getOutputDirectory(requireActivity())
                val outputFile = File(outputDirectory, fileName)

//                val lensFacing = CameraSelector.LENS_FACING_BACK // or CameraSelector.LENS_FACING_FRONT
                val lensFacing = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
                // Setup image capture metadata
                val metadata = ImageCapture.Metadata().apply {
                    // Mirror image when using the front camera
//                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_BACK
                    isReversedHorizontal = lensFacing == CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
                }

                // Create output options object which contains file + metadata
                val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile)
                    .setMetadata(metadata)
                    .build()

                // Take the picture and save to the output file
                imageCapture.takePicture(
                    outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            // Image capture is successful, resize the captured image
                            val capturedImageBitmap = BitmapFactory.decodeFile(outputFile.absolutePath)
                            val resizedImageBitmap = Bitmap.createScaledBitmap(capturedImageBitmap, 256, 256, true)
                            val bitmapString = bitmapToString(resizedImageBitmap)
                            // Use the resizedImageUri as needed (e.g., pass it to your machine learning model or display it)

                            val savedUri = Uri.fromFile(outputFile)
                            viewModel.savePhoto(savedUri, requireContext(), requireActivity(), resizedImageBitmap)
                            viewModel.outputFilePath.observe(viewLifecycleOwner) { filePath ->
                                if (filePath != null) {
                                    mediaScanner(filePath)
//                                    navigateToDiagResultFragment(savedUri.toString())
                                    navigateToDiagResultFragment(bitmapString)
                                    Toast.makeText(requireActivity(), "can save?", Toast.LENGTH_SHORT).show()
                                } else {
                                    // handle null file path
                                    Toast.makeText(requireActivity(), "error save?", Toast.LENGTH_SHORT).show()
                                    Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!! Well bye byeee no filepath sadge")
                                }
                            }

                            // Show success message
                            Toast.makeText(requireContext(), "Photo saved to: ${outputFile.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                        override fun onError(exception: ImageCaptureException) {
                            // Show error message
                            Toast.makeText(requireContext(), "Error saving photo: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    }


    fun bitmapToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private val cameraProviderResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permissionGranted->
        if(permissionGranted){
            // cut and paste the previous startCamera() call here.
            startCamera()
        }else {
            Snackbar.make(binding.root,"The camera permission is required", Snackbar.LENGTH_INDEFINITE).show()
        }
    }
}