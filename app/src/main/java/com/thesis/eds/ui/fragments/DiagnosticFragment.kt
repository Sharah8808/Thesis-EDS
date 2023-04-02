package com.thesis.eds.ui.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentDiagnosticBinding
import com.thesis.eds.interfaces.ActionBarTitleSetter
import com.thesis.eds.interfaces.MenuItemHighlighter
import com.thesis.eds.ui.viewModels.DiagnosticViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class DiagnosticFragment : Fragment() {

    private var _binding: FragmentDiagnosticBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DiagnosticViewModel>()
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector

    private var imageCapture: ImageCapture? = null
    private lateinit var imgCaptureExecutor: ExecutorService

    private val cameraProviderResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permissionGranted->
        if(permissionGranted){
            // cut and paste the previous startCamera() call here.
            startCamera()
        }else {
            Snackbar.make(binding.root,"The camera permission is required", Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiagnosticBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Toast.makeText(requireActivity(), "hoopplaaaa", Toast.LENGTH_SHORT).show()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val previewView : PreviewView = binding.cameraPreview


        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProviderResult.launch(android.Manifest.permission.CAMERA)

        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        startCamera()
        binding.fabPhotoShot.setOnClickListener{
            takePhoto()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                animateFlash()
            }
        }
    }

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
                // binding the lifecycle of the camera to the lifecycle of the application.
//                cameraProvider.bindToLifecycle(this,cameraSelector,preview)
                cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
            } catch (e: Exception) {
                Log.d(TAG, "Use case binding failed")
            }

        },ContextCompat.getMainExecutor(requireContext()))
    }

//    private fun takePhoto(){
//        imageCapture?.let{
//            // Create a Firebase Storage reference with a unique name
//            val storageRef = Firebase.storage.reference
//            val fileName = "images/JPEG_${System.currentTimeMillis()}"
//            val imageRef = storageRef.child(fileName)
//
//            // Take the picture and upload it to Firebase Storage
//            it.takePicture(imgCaptureExecutor, object : ImageCapture.OnImageCapturedCallback() {
//                override fun onCaptureSuccess(image: ImageProxy) {
//                    val buffer = image.planes[0].buffer
//                    val bytes = ByteArray(buffer.capacity()).apply { buffer.get(this) }
//                    val metadata = StorageMetadata.Builder().setContentType("image/jpeg").build()
//                    imageRef.putBytes(bytes, metadata)
//                        .addOnSuccessListener {
//                            Log.d(TAG, "Image uploaded successfully to Firebase Storage with path: ${imageRef.path}")
//                        }
//                        .addOnFailureListener { exception ->
//                            Log.e(TAG, "Error uploading image to Firebase Storage: ${exception.message}")
//                        }
//                    image.close()
//                }
//
//                override fun onError(exception: ImageCaptureException) {
//                    Toast.makeText(
//                        binding.root.context,
//                        "Error taking photo",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    Log.d(TAG, "Error taking photo:$exception")
//                }
//            })
//        }
//    }

//    private fun takePhoto(){
//        imageCapture?.let{
//            //Create a storage location whose fileName is timestamped in milliseconds.
//            val fileName = "JPEG_${System.currentTimeMillis()}"
//            val file = File(externalMediaDirs[0],fileName)
//
//            // Save the image in the above file
//            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
//
//            /* pass in the details of where and how the image is taken.(arguments 1 and 2 of takePicture)
//            pass in the details of what to do after an image is taken.(argument 3 of takePicture) */
//
//            it.takePicture(
//                outputFileOptions,
//                imgCaptureExecutor,
//                object : ImageCapture.OnImageSavedCallback {
//                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults){
//                        Log.i(TAG,"The image has been saved in ${file.toUri()}")
//                    }
//
//                    override fun onError(exception: ImageCaptureException) {
//                        Toast.makeText(
//                            binding.root.context,
//                            "Error taking photo",
//                            Toast.LENGTH_LONG
//                        ).show()
//                        Log.d(TAG, "Error taking photo:$exception")
//                    }
//
//                })
//        }
//    }

    private fun takePhoto(){
        imageCapture?.let{ imageCapture ->
            // Create the output file with a unique name
            val fileName = "JPEG_${System.currentTimeMillis()}.jpg"
            val outputDirectory = getOutputDirectory()
            val outputFile = File(outputDirectory, fileName)
            val lensFacing = CameraSelector.LENS_FACING_BACK // or CameraSelector.LENS_FACING_FRONT
            // Setup image capture metadata
            val metadata = ImageCapture.Metadata().apply {
                // Mirror image when using the front camera
                isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_BACK
            }

            // Create output options object which contains file + metadata
            val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile)
                .setMetadata(metadata)
                .build()

            // Take the picture and save to the output file
            imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        //---------------
                        MediaScannerConnection.scanFile(
                            requireContext(),
                            arrayOf(outputFile.absolutePath),
                            null,
                            object : MediaScannerConnection.OnScanCompletedListener {
                                override fun onScanCompleted(path: String?, uri: Uri?) {
                                    Log.d(TAG, "Scanned $path")
                                }
                            }
                        )

                        //-----------------
//                        val contentValues = ContentValues().apply {
//                            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
//                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//                        }
//                        val uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//                        uri?.let {
//                            try {
//                                requireContext().contentResolver.openOutputStream(uri).use { outputStream ->
//                                    val inputStream = FileInputStream(outputFile)
//                                    inputStream.copyTo(outputStream!!)
//                                    inputStream.close()
//                                }
//                                Log.d(TAG, "Saved image to gallery: ${uri.toString()}")
//                            } catch (e: IOException) {
//                                Log.e(TAG, "Error saving image to gallery", e)
//                            }
//                        }



                        // Show success message
                        Toast.makeText(requireContext(), "Photo saved to: ${outputFile.absolutePath}", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "Photo saved to: ${outputFile.absolutePath}")
                    }

                    override fun onError(exception: ImageCaptureException) {
                        // Show error message
                        Toast.makeText(requireContext(), "Error saving photo: ${exception.message}", Toast.LENGTH_LONG).show()
                        Log.e(TAG, "Error saving photo", exception)
                    }
                }
            )
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
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
}