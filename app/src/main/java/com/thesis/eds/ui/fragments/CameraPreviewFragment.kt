package com.thesis.eds.ui.fragments

import android.app.Dialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentCameraPreviewBinding
import java.io.File
import java.io.FileInputStream

class CameraPreviewFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCameraPreviewBinding
    private lateinit var imageBitmap: Bitmap
    private val args : CameraPreviewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = args.imgByte as? Bundle
        val imageData = bundle?.getByteArray("imageData")
//        Log.d(ContentValues.TAG, "///////////////////////-------------------------------------- The image dataaa ??? ${arguments?.getByteArray("imageData")}")
        Log.d(ContentValues.TAG, "///////////////////////-------------------------------------- The bundleee ??? ${bundle}")
        Log.d(ContentValues.TAG, "///////////////////////-------------------------------------- The image dataaa ??? ${imageData}")

// Convert the byte array to a Bitmap
        if (imageData != null) {
            imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            binding.imgCameraPreview.setImageBitmap(imageBitmap)
        } else {
            Glide.with(this)
                .load(R.drawable.dislist_yuuya)
                .transform(RoundedCorners(20))
                .into(binding.imgCameraPreview)
            // handle the case where the image data is null
        }

        binding.buttonYes.setOnClickListener {
            // Save the image to the gallery
//            saveImageToGallery(imageBitmap)
            // Dismiss the bottom sheet dialog
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.AppTheme_BottomSheetDialog)
        dialog.setContentView(R.layout.fragment_camera_preview)

        //Get the screen height
        val screenHeight = resources.displayMetrics.heightPixels

        // Set the peek height to a value that is tall enough to show all the views
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { it2 ->
                val peekHeight = screenHeight - resources.getDimensionPixelSize(R.dimen.bottom_sheet_margin_bottom)
                BottomSheetBehavior.from(it2).peekHeight = peekHeight
//                BottomSheetBehavior.from(view?.parent as View).peekHeight = peekHeight
//                BottomSheetBehavior.from(it2).peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)
            }
        }

        // ...
        return dialog
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        // Save the bitmap to the gallery
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image.jpeg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
//        val uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//        uri?.let {
//            try {
//                requireContext().contentResolver.openOutputStream(uri).use { outputStream ->
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//                }
//                Toast.makeText(requireContext(), "Image saved to gallery", Toast.LENGTH_SHORT).show()
//            } catch (e: IOException) {
//                Toast.makeText(requireContext(), "Failed to save image to gallery", Toast.LENGTH_SHORT).show()
//            }
//        }
    }
}
