package com.thesis.eds.ui.fragments

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentDiagnosticResultBinding
import com.thesis.eds.interfaces.ActionBarTitleSetter
import com.thesis.eds.ui.viewModels.DiagnosticResultViewModel

class DiagnosticResultFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDiagnosticResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DiagnosticResultViewModel>()
    private val args : CameraPreviewFragmentArgs by navArgs()
    private var uri : Uri? = null
    private var finalResult : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiagnosticResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uri = Uri.parse(args.uriArgs)
        Glide.with(this)
            .load(uri)
            .transform(RoundedCorners(20))
            .into(binding.imgResult)

        val noButton : Button = view.findViewById(binding.buttonNo.id)
        val yesButton : Button = view.findViewById(binding.buttonYes.id)
        val saveButton : Button = view.findViewById(binding.buttonSave.id)
        noButton.setOnClickListener(this)
        yesButton.setOnClickListener(this)
        saveButton.setOnClickListener(this)
        beforeActionVisibility()

    }

    override fun onClick(v: View) {
        when(v.id){
            binding.buttonNo.id -> {
                showResultDiagnoseDialog{ actualResult ->
                    //man, we gotta do something bout this predict variable in the future
                    finalResult = actualResult
                    afterActionVisibility(actualResult)
                }
            }
            binding.buttonYes.id -> {
                val predictResult = binding.txtPredict.text.toString()
                finalResult = predictResult
                afterActionVisibility(predictResult)
            }
            binding.buttonSave.id -> {
                if(binding.buttonSave.isClickable){
                    val predictResult = binding.txtPredict.text.toString()
                    viewModel.createNewHistory(predictResult,finalResult!!,uri!!)

                    val action = DiagnosticResultFragmentDirections.actionDiagnosticResultFragmentToNavHome()
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun showResultDiagnoseDialog(callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Tulis hasil diagnosa")

        // Set up the input
        val input = EditText(requireContext())
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Simpan") { _, _ ->
            // Call the callback function with the text from the input field
            callback(input.text.toString())
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }

        // Show the dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun beforeActionVisibility(){
        binding.buttonSave.isClickable = false
        binding.buttonSave.isFocusable = false
        binding.buttonSave.isEnabled = false
    }

    private fun afterActionVisibility(result : String){
        binding.buttonNo.visibility = View.INVISIBLE
        binding.buttonYes.visibility = View.INVISIBLE
        binding.desc1.visibility = View.INVISIBLE
        binding.desc2.visibility = View.INVISIBLE

        binding.buttonSave.isClickable = true
        binding.buttonSave.isFocusable = true
        binding.buttonSave.isEnabled = true

        binding.txtPredict.text = result
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.koreksi_diagnosa))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}