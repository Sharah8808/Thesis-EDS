package com.thesis.eds.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentCameraPreviewBinding

class CameraPreviewFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCameraPreviewBinding
    private lateinit var imageBitmap: Bitmap
    private val args : CameraPreviewFragmentArgs by navArgs()
    private lateinit var parentContext: Context
    private lateinit var historyName : String
//    private val viewModel by viewModels<CameraPreviewViewModel>()

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

        val uri = args.uriArgs
        Glide.with(this)
            .load(uri)
            .transform(RoundedCorners(20))
            .into(binding.imgCameraPreview)

        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                showExitAlertDialog(parentContext)
                true
            } else {
                false
            }
        }

        binding.buttonNo.setOnClickListener{
            val action = CameraPreviewFragmentDirections.actionCameraPreviewFragmentToNavDiagnostic()
            findNavController().navigate(action)
        }

        binding.buttonYes.setOnClickListener{
            val action = CameraPreviewFragmentDirections.actionCameraPreviewFragmentToDiagnosticResultFragment(uri)
            findNavController().navigate(action)
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

    private fun showExitAlertDialog(context : Context) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Confirmation")
            alertDialogBuilder.setMessage("Kembali ke halaman sebelumnya?")
                .setCancelable(false)
                .setPositiveButton("Iya") { _, _ ->
                    // Go back to the previous fragment and lose the current picture data
                    val action = CameraPreviewFragmentDirections.actionCameraPreviewFragmentToNavDiagnostic()
                    findNavController().navigate(action)
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentContext = requireActivity()
    }

}
