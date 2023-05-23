package com.thesis.eds.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    private var unsavedChanges = false
    private var arrModelResults : ArrayList<String> = ArrayList()
    private var arrModelPercent : ArrayList<String> = ArrayList()

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
        bitmap?.let { mlModelOperationsSecond(it) }

        binding.txtPredict.text = arrModelResults[0]
        binding.txtPredict1.text = arrModelResults[0]
        binding.txtPredict2.text = arrModelResults[1]
        binding.txtPredict3.text = arrModelResults[2]
        binding.txtPredictPercentage.text = arrModelPercent[0]
        binding.txtPredictPercentage2.text = arrModelPercent[1]
        binding.txtPredictPercentage3.text = arrModelPercent[2]

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
                    val predictResult = arrModelResults[0]
                    Log.d("EDSThesis_DResult", "Variables check --> Predict result = $predictResult | Final result = $finalResult || Uri = $uri ----------")
                    viewModel.createNewHistory(predictResult,finalResult!!,uri!!)

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

        // Set up the AutoCompleteTextView
        val autoCompleteTextView = AutoCompleteTextView(requireContext())
        val diseaseList = Dummy.getDummyDiseaseList().map { it.name_disease_list }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, diseaseList)
        autoCompleteTextView.setAdapter(adapter)
        builder.setView(autoCompleteTextView)

        // Set up the buttons
        builder.setPositiveButton("Simpan") { _, _ ->
            // Call the callback function with the text from the AutoCompleteTextView
            val text = autoCompleteTextView.text.toString()
            callback(text)
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }

        // Show the dialog
        val dialog = builder.create()
        dialog.show()
    }


    private fun toPercentage(n: Float, digits: Int): String {
        return String.format("%." + digits + "f", n * 100) + "%"

    }

    private fun mlModelOperationsSecond(bitmap: Bitmap) {
        val model = EarDiseaseModel.newInstance(requireContext())
        // Convert the input bitmap to a TensorImage
        val inputTensorImage = bitmapToTensorBuffer(bitmap)

        // Perform the machine learning operations using the inputTensorImage
        val results = model.process(inputTensorImage)
        val outputFeature0 = results.outputFeature0AsTensorBuffer

        // Get the float array from the output tensor buffer
        val outputArray = outputFeature0.floatArray

        var maxIndexx = -1
        var secondMaxIndex = -1
        var thirdMaxIndex = -1
        var maxConfidencee = Float.MIN_VALUE
        var secondMaxConfidence = Float.MIN_VALUE
        var thirdMaxConfidence = Float.MIN_VALUE

        for (i in outputArray.indices) {
            val confidence = outputArray[i]

            if (confidence > maxConfidencee) {
                thirdMaxIndex = secondMaxIndex
                thirdMaxConfidence = secondMaxConfidence
                secondMaxIndex = maxIndexx
                secondMaxConfidence = maxConfidencee
                maxIndexx = i
                maxConfidencee = confidence
            } else if (confidence > secondMaxConfidence) {
                thirdMaxIndex = secondMaxIndex
                thirdMaxConfidence = secondMaxConfidence
                secondMaxIndex = i
                secondMaxConfidence = confidence
            } else if (confidence > thirdMaxConfidence) {
                thirdMaxIndex = i
                thirdMaxConfidence = confidence
            }
        }

        Log.d("EDSThesis_DResult", "Max Index         = $maxIndexx, Confidence: $maxConfidencee")
        Log.d("EDSThesis_DResult", "Second Max Index  = $secondMaxIndex, Confidence: $secondMaxConfidence")
        Log.d("EDSThesis_DResult", "Third Max Index   = $thirdMaxIndex, Confidence: $thirdMaxConfidence")

        // Define list of class labels
        val classLabels = Dummy.getDummyDiseaseList().map { it.name_disease_list }

        classLabels[maxIndexx]?.let { arrModelResults.add(it) }
        classLabels[secondMaxIndex]?.let { arrModelResults.add(it) }
        classLabels[thirdMaxIndex]?.let { arrModelResults.add(it) }
        arrModelPercent.add(toPercentage(maxConfidencee, 1))
        arrModelPercent.add(toPercentage(secondMaxConfidence, 2))
        arrModelPercent.add(toPercentage(thirdMaxConfidence, 3))

        for(i in arrModelResults){
            Log.d("EDSThesis_DResult", "Variable check --> model result array = $i")
        }
        for(i in arrModelPercent){
            Log.d("EDSThesis_DResult", "Variable check --> model percentage array = $i")
        }

        // Releases model resources if no longer used.
        model.close()
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

        binding.txtPredictPercentage.visibility = View.INVISIBLE
        binding.txtPredictPercentage2.visibility = View.INVISIBLE
        binding.txtPredictPercentage3.visibility = View.INVISIBLE
        binding.txtPredict1.visibility = View.INVISIBLE
        binding.txtPredict2.visibility = View.INVISIBLE
        binding.txtPredict3.visibility = View.INVISIBLE

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