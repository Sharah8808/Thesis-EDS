package com.thesis.eds.ui.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.activity.addCallback
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentDiagnosticBinding
import com.thesis.eds.interfaces.ActionBarTitleSetter
import com.thesis.eds.interfaces.MenuItemHighlighter
import com.thesis.eds.ui.viewModels.DiagnosticViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DiagnosticFragment : Fragment() {

    private var _binding: FragmentDiagnosticBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private val viewModel by viewModels<DiagnosticViewModel>()
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

    private val choosePictureGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.savePhoto(uri, requireContext(), requireActivity())
            viewModel.outputFilePath.observe(viewLifecycleOwner) { filePath ->
                if (filePath != null) {
                    mediaScanner(filePath)
                    val bundle = bundlingImage(filePath)
                    navigateToCamPreview(bundle)
                } else {
                    // handle null file path
                    Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!! Well bye byeee no filepath sadge")
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
        val root: View = binding.root
        Toast.makeText(requireActivity(), "hoopplaaaa", Toast.LENGTH_SHORT).show()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val navController = findNavController()
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProviderResult.launch(android.Manifest.permission.CAMERA)

        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        lifecycleScope.launch {
            startCamera()
        }

        binding.buttonShot.setOnClickListener{
            val currentFragmentId = navController.currentDestination?.id
            Log.d("Current Fragment ID", currentFragmentId.toString() + " the current frag id ==========================================")
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

    private fun choosePictureFromGallery() {
        choosePictureGallery.launch("image/*")
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
                cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
            } catch (e: Exception) {
                Log.d(TAG, "Use case binding failed")
            }

        },ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto(){
        lifecycleScope.launch {
            imageCapture?.let{ imageCapture ->
                // Create the output file with a unique name
                val fileName = "JPEG_${System.currentTimeMillis()}.jpg"
//            val outputDirectory = getOutputDirectory()
                val outputDirectory = viewModel.getOutputDirectory(requireActivity())
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

                            val savedUri = Uri.fromFile(outputFile)
                            viewModel.savePhoto(savedUri, requireContext(), requireActivity())
                            viewModel.outputFilePath.observe(viewLifecycleOwner) { filePath ->
                                if (filePath != null) {
                                    mediaScanner(filePath)
                                    val bundle = bundlingImage(filePath)
                                    navigateToCamPreview(bundle)
                                } else {
                                    // handle null file path
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

    private fun bundlingImage(filePath : String): Bundle{
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

    private fun navigateToCamPreview(bundle: Bundle){
        // Create the navigation action and pass the bundle as an argument
        val action = DiagnosticFragmentDirections.actionNavDiagnosticToCameraPreviewFragment(bundle)
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
}