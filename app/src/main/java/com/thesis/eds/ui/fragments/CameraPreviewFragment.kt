package com.thesis.eds.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentCameraPreviewBinding
import com.thesis.eds.ui.activities.MainActivity
import java.io.File
import java.io.FileInputStream

class CameraPreviewFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCameraPreviewBinding
    private lateinit var imageBitmap: Bitmap
    private val args : CameraPreviewFragmentArgs by navArgs()
    private lateinit var parentContext: Context


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

        dialog?.setCancelable(true)

//        dialog?.setOnCancelListener {
//                showAlertDialog(parentContext)
//        }


        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                showAlertDialog(parentContext)
                true
            } else {
                false
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.AppTheme_BottomSheetDialog)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.fragment_camera_preview)

        //Get the screen height
        val screenHeight = resources.displayMetrics.heightPixels

        // Set the peek height to a value that is tall enough to show all the views
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { it2 ->
                val peekHeight = screenHeight - resources.getDimensionPixelSize(R.dimen.bottom_sheet_margin_bottom)
                BottomSheetBehavior.from(it2).peekHeight = peekHeight
            }
        }

        return dialog
    }

    private fun showAlertDialog(context : Context) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            // rest of your code
            alertDialogBuilder.setTitle("Confirmation")
//            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setMessage("Kembali ke halaman sebelumnya?")
                .setCancelable(false)
                .setPositiveButton("Iya") { _, _ ->
                    // Go back to the previous fragment and lose the current picture data
//                    requireActivity().onBackPressed()
                    val action = CameraPreviewFragmentDirections.actionCameraPreviewFragmentToNavDiagnostic()
                    findNavController().navigate(action)
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }

            val alertDialog = alertDialogBuilder.create()
//            dialog?.setCancelable(false)
//            dialog?.setCanceledOnTouchOutside(true)
            alertDialog.show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentContext = requireActivity()
    }

}
