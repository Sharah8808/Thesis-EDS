package com.thesis.eds.ui.fragments

import android.app.AlertDialog
import android.content.ContentValues
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
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DiagnosticResultFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDiagnosticResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DiagnosticResultViewModel>()
    private val args : DiagnosticResultFragmentArgs by navArgs()
    private var uri : Uri? = null
    private var finalResult : String? = null
    private var modelPredictResult : String? = null
    private var onBackPressedCallback: OnBackPressedCallback? = null

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

//        uri = Uri.parse(args.uriArgs)
//
//        showBottomSheetDialog(uri!!)
//
//        Glide.with(this)
//            .load(uri)
//            .transform(RoundedCorners(20))
//            .into(binding.imgResult)
        val argsString = args.uriArgs
        val bitmap = stringToBitmap(argsString)

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

//        bitmap?.let { mlTryHard(it) }
        modelPredictResult = bitmap?.let { mlModelOperationsSecond(it) }
        binding.txtPredict.text = modelPredictResult


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(ContentValues.TAG, " 11ini back pressed fri onCreat kepanggil ga sih               ----------------------------------------------")
                DialogUtils.showExitAlertDialog(requireContext()){
                    findNavController().navigateUp()
                }
            }
        }.apply { isEnabled = true }
        )
        Log.d(ContentValues.TAG, " di onviewcreatedcurrent navcont = ${findNavController().currentDestination?.id}          !!!  ---------------------------------------------- r.id diagresult frag? ${R.id.diagnosticResultFragment}")

    }

    override fun onClick(v: View) {
        when(v.id){
            binding.buttonNo.id -> {
                showResultDiagnoseDialog{ actualResult ->
                    //man, we gotta do something bout this predict variable in the future
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
                    val predictResult = binding.txtPredict.text.toString()
                    viewModel.createNewHistory(predictResult,finalResult!!,uri!!)

                    val action = DiagnosticResultFragmentDirections.actionDiagnosticResultFragmentToNavHome()
                    findNavController().navigate(action)
                }
            }
        }

    }

    private fun showBottomSheetDialog(bitmap : Bitmap) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        Log.d(ContentValues.TAG, "what is the uri to bottomsheeet ?? = $uri  || and bitmaapp == $bitmap             ----------------------------------------------")
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.setContentView(R.layout.fragment_camera_preview)

        //Get the screen height
        val screenHeight = resources.displayMetrics.heightPixels

        bottomSheetDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        bottomSheetDialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
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
                    bottomSheetDialog.dismiss()
                }

                noButton.setOnClickListener{
                    requireActivity().onBackPressed()
                    bottomSheetDialog.dismiss()
                }

                it2.setOnKeyListener { _, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                        requireActivity().onBackPressed()
                        bottomSheetDialog.dismiss()
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

//    private fun mlModelOperations() : String? {
//
//        // Assume `uri` is the Uri object of the image
//        val inputStream = uri?.let { context?.contentResolver?.openInputStream(it) }
//        val bytes = ByteArrayOutputStream()
//        inputStream?.copyTo(bytes)
//
//        val bufferSize = 1 * 256 * 256 * 3 * 4
//        val buffer = ByteBuffer.allocateDirect(bufferSize)
//        buffer.order(ByteOrder.nativeOrder())
//
//        val bufferArray = bytes.toByteArray()
//        if (bufferArray.size != bufferSize) {
//            // Handle the error condition
//            Log.d(ContentValues.TAG, " buffer errorrrr sizeee      bufferArray == ${bufferArray.size} || bufferSize == $bufferSize         -------------------------------------------------")
//        }
//        buffer.put(bufferArray)
//        buffer.rewind()
//
//        val model = EarDiseaseModel.newInstance(requireContext())
//
//        // Creates inputs for reference.
//        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 256, 256, 3), DataType.FLOAT32)
//        inputFeature0.loadBuffer(buffer)
//
//        // Runs model inference and gets result.
//        val outputs = model.process(inputFeature0)
//        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
//
//        // Get the float array from the output tensor buffer
//        val outputArray = outputFeature0.floatArray
//        for (i in outputArray.indices) {
//            Log.d(ContentValues.TAG, "outputArray[$i] = ${outputArray[i]} -------------------------------------------------")
//        }
//
//        // Find index of maximum probability
//        var maxIndex = 0
//        var maxValue = outputArray[0]
//        for (i in 1 until outputArray.size) {
//            Log.d(ContentValues.TAG, " Current outputarray $i == ${outputArray[i]}   !!  maxvalue == $maxValue         ---------------------------------------------- $i")
//            if (outputArray[i] > maxValue) {
//                maxIndex = i
//                maxValue = outputArray[i]
//            }
//        }
//
//        // Define list of class labels
//        val classLabels = Dummy.getDummyDiseaseList().map { it.name_disease_list }
//
//        // Get predicted class label
//        val predictedClass = classLabels[maxIndex]
//
//
//        // Releases model resources if no longer used.
//        model.close()
//
//        return predictedClass
//    }

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
            Log.d(ContentValues.TAG, "outputArray[$i] = ${outputArray[i]} -------------------------------------------------")
        }

        // Find index of maximum probability
        var maxIndex = 0
        var maxValue = outputArray[0]
        for (i in 1 until outputArray.size) {
            Log.d(ContentValues.TAG, " Current outputarray $i == ${outputArray[i]}   !!  maxvalue == $maxValue         ---------------------------------------------- $i")
            if (outputArray[i] > maxValue) {
                maxIndex = i
                maxValue = outputArray[i]
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

//    private fun mlModelOperations(bitmap: Bitmap): String? {
//        // Convert the Bitmap to a ByteBuffer
//        val byteBuffer = convertBitmapToByteBuffer(bitmap)
//
//        // Perform your machine learning operations using the byteBuffer
//
//        // Return the result or null
//        return null
//    }

//    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
//        val byteBuffer = ByteBuffer.allocateDirect(BATCH_SIZE * IMAGE_SIZE * IMAGE_SIZE * CHANNELS)
//        byteBuffer.order(ByteOrder.nativeOrder())
//
//        // Scale the input Bitmap to the desired image size
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)
//
//        // Convert the scaledBitmap to grayscale and normalize pixel values
//        val grayscaleBitmap = convertToGrayscale(scaledBitmap)
//        val normalizedBitmap = normalizeBitmap(grayscaleBitmap)
//
//        // Convert the normalizedBitmap to ByteBuffer
//        val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)
//        normalizedBitmap.getPixels(intValues, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE)
//
//        for (pixelValue in intValues) {
//            val normalizedPixelValue = (pixelValue shr 16 and 0xFF) / 255.0f
//            byteBuffer.putFloat(normalizedPixelValue)
//        }
//
//        return byteBuffer
//    }
//
//    private fun convertToGrayscale(bitmap: Bitmap): Bitmap {
//        val grayscaleBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(grayscaleBitmap)
//        val paint = Paint().apply {
//            colorMatrix = ColorMatrix().apply {
//                setSaturation(0f)
//            }
//        }
//        canvas.drawBitmap(bitmap, 0f, 0f, paint)
//        return grayscaleBitmap
//    }
//
//    private fun normalizeBitmap(bitmap: Bitmap): Bitmap {
//        val normalizedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
//        val minPixelValue = 0f
//        val maxPixelValue = 255f
//        val delta = maxPixelValue - minPixelValue
//
//        for (y in 0 until bitmap.height) {
//            for (x in 0 until bitmap.width) {
//                val pixelValue = bitmap.getPixel(x, y)
//                val normalizedPixelValue = (Color.red(pixelValue) - minPixelValue) / delta
//                val normalizedColor = Color.rgb(
//                    (normalizedPixelValue * 255).toInt(),
//                    (normalizedPixelValue * 255).toInt(),
//                    (normalizedPixelValue * 255).toInt()
//                )
//                normalizedBitmap.setPixel(x, y, normalizedColor)
//            }
//        }
//
//        return normalizedBitmap
//    }



//    private fun mlTryHard(bitmap : Bitmap){
//        // Step 1: create TFLite's TensorImage object
//        val image = TensorImage.fromBitmap(bitmap)
//
//        // Step 2: Initialize the detector object
//        val options = ObjectDetector.ObjectDetectorOptions.builder()
//            .setMaxResults(5)
//            .setScoreThreshold(0.5f)
//            .build()
//        val detector = ObjectDetector.createFromFileAndOptions(
//            requireContext(), // the application context
//            "EarDiseaseModel.tflite", // must be same as the filename in assets folder
//            options
//        )
//
//        // Step 3: feed given image to the model and print the detection result
//        val results = detector.detect(image)
//
//        // Step 4: Parse the detection result and show it
//        debugPrint(results)
//    }
//
//    private fun debugPrint(results : List<Detection>) {
//        for ((i, obj) in results.withIndex()) {
//            val box = obj.boundingBox
//
//            Log.d(TAG, "Detected object: ${i} ")
//            Log.d(TAG, "  boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})")
//
//            for ((j, category) in obj.categories.withIndex()) {
//                Log.d(TAG, "    Label $j: ${category.label}")
//                val confidence: Int = category.score.times(100).toInt()
//                Log.d(TAG, "    Confidence: ${confidence}%")
//            }
//        }
//    }



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

    fun stringToBitmap(encodedString: String): Bitmap? {
        val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
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