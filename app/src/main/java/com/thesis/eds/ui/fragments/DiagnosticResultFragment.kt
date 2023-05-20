package com.thesis.eds.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentDiagnosticResultBinding
import com.thesis.eds.ml.EarDiseaseModel
import com.thesis.eds.ui.viewModels.DiagnosticResultViewModel
import com.thesis.eds.utils.DialogUtils
import com.thesis.eds.utils.Dummy
import com.thesis.eds.utils.interfaces.ActionBarTitleSetter
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class DiagnosticResultFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDiagnosticResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DiagnosticResultViewModel>()
    private val args : DiagnosticResultFragmentArgs by navArgs()
    private var uri : Uri? = null
    private var finalResult : String? = null
    private var modelPredictResult : String? = null
    private var unsavedChanges = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiagnosticResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bitmapArgsString = args.bitmapArgs
        val bitmap = stringToBitmap(bitmapArgsString)

        uri = Uri.parse(args.uriArgs)

        Glide.with(this)
            .load(bitmap)
            .transform(RoundedCorners(20))
            .into(binding.imgResult)

        bitmap?.let { showBottomSheetDialog(it) }

        val noButton : Button = view.findViewById(binding.buttonNo.id)
        val yesButton : Button = view.findViewById(binding.buttonYes.id)
        val saveButton : Button = view.findViewById(binding.buttonSave.id)
        noButton.setOnClickListener(this)
        yesButton.setOnClickListener(this)
        saveButton.setOnClickListener(this)
        beforeActionVisibility()

        unsavedChanges = true

        modelPredictResult = bitmap?.let { mlModelOperationsSecond(it) }
        binding.txtPredict.text = modelPredictResult

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("EDSThesis_DResult", "Back to previous page, data will be unsaved.")
                DialogUtils.showExitAlertDialog(requireContext()){
                    findNavController().navigateUp()
                }
            }
        }.apply { isEnabled = true }
        )

    }

    override fun onClick(v: View) {
        when(v.id){
            binding.buttonNo.id -> {
                showResultDiagnoseDialog{ actualResult ->
                    finalResult = actualResult
                    afterActionVisibility(actualResult)
                    unsavedChanges = false
                }
            }
            binding.buttonYes.id -> {
                val predictResult = binding.txtPredict.text.toString()
                finalResult = predictResult
                afterActionVisibility(predictResult)
                unsavedChanges = false
            }
            binding.buttonSave.id -> {
                if(binding.buttonSave.isClickable){
                    val predictResult = modelPredictResult
                    Log.d("EDSThesis_DResult", "Variables check --> Predict result = $predictResult | Final result = $finalResult || Uri = $uri ----------")
                    viewModel.createNewHistory(predictResult!!,finalResult!!,uri!!)

                    val action = DiagnosticResultFragmentDirections.actionDiagnosticResultFragmentToNavHome()
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun showBottomSheetDialog(bitmap : Bitmap) {
        Log.d("EDSThesis_DResult", "Showing bottom sheet dialog...")
        Log.d("EDSThesis_DResult", "Variables check--> Uri = $uri | Bitmap == $bitmap ----------")

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.setContentView(R.layout.fragment_camera_preview)

        //Get the screen height
        val screenHeight = resources.displayMetrics.heightPixels

        bottomSheetDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        bottomSheetDialog.setOnShowListener {
            val dialogInterface = it as BottomSheetDialog
            val bottomSheet = dialogInterface.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { it2 ->
                val peekHeight = screenHeight - resources.getDimensionPixelSize(R.dimen.bottom_sheet_margin_bottom)
                BottomSheetBehavior.from(it2).peekHeight = peekHeight

                val resultImg = it2.findViewById<ImageView>(R.id.img_camera_preview)
                val yesButton = it2.findViewById<Button>(R.id.button_yes)
                val noButton = it2.findViewById<Button>(R.id.button_no)

                Glide.with(this)
                    .load(bitmap)
                    .transform(RoundedCorners(20))
                    .into(resultImg)

                yesButton.setOnClickListener{
                    dialogInterface.dismiss()
                }

                noButton.setOnClickListener{
                    requireActivity().onBackPressed()
                    dialogInterface.dismiss()
                }

                it2.setOnKeyListener { _, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                        requireActivity().onBackPressed()
                        dialogInterface.dismiss()
                        true
                    } else {
                        false
                    }
                }
            }
        }
        bottomSheetDialog.show()
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

    private fun mlModelOperationsSecond(bitmap: Bitmap): String? {
        val model = EarDiseaseModel.newInstance(requireContext())
        // Convert the input bitmap to a TensorImage
        val inputTensorImage = bitmapToTensorBuffer(bitmap)

        // Perform the machine learning operations using the inputTensorImage
        val results = model.process(inputTensorImage)
        val outputFeature0 = results.outputFeature0AsTensorBuffer

        // Get the float array from the output tensor buffer
        val outputArray = outputFeature0.floatArray
        for (i in outputArray.indices) {
            Log.d("EDSThesis_DResult", "Model's outputArray[$i] = ${outputArray[i]} --")
        }

        // Find index of maximum probability
        var maxIndex = -1
        var maxConfidence = Float.MIN_VALUE
        for (i in outputArray.indices) {
            val confidence = outputArray[i]
            Log.d("EDSThesis_DResult", "Model's current outputArray $i = ${outputArray[i]} | Max confidence = $maxConfidence | MaxIndex = $maxIndex---")
            if (confidence >= maxConfidence) {
                maxConfidence = confidence
                maxIndex = i
            }
        }

        // Define list of class labels
        val classLabels = Dummy.getDummyDiseaseList().map { it.name_disease_list }

        // Get predicted class label
        val predictedClass = classLabels[maxIndex]

        // Releases model resources if no longer used.
        model.close()

        return predictedClass
    }

    private fun bitmapToTensorBuffer(bitmap: Bitmap): TensorBuffer {
        // Resize the bitmap to match the model's input size (256x256)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true)

        // Create a TensorImage object from the resized bitmap
        val tensorImage = TensorImage.fromBitmap(resizedBitmap)

        // Create a TensorBuffer with the same shape as the model's input tensor
        val tensorBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 256, 256, 3), DataType.FLOAT32)

        // Normalize the pixel values of the tensor image
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(256, 256, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()

        val preprocessedImage = imageProcessor.process(tensorImage)
        val preprocessedBuffer = preprocessedImage.buffer
        val tensorBufferBuffer = tensorBuffer.buffer

        preprocessedBuffer.rewind()
        preprocessedBuffer.limit(tensorBufferBuffer.capacity())
        tensorBufferBuffer.rewind()
        tensorBufferBuffer.put(preprocessedBuffer)

        return tensorBuffer
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

    private fun stringToBitmap(encodedString: String): Bitmap? {
        val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    override fun onAttach(context: Context) {
        Log.d("EDSThesis_DResult", "Currently on Diagnostic Result Fragment...")
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.koreksi_diagnosa))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}