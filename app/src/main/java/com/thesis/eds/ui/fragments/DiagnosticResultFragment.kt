package com.thesis.eds.ui.fragments

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.navigateUp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentDiagnosticResultBinding
import com.thesis.eds.interfaces.ActionBarTitleSetter
import com.thesis.eds.ui.viewModels.DiagnosticResultViewModel
import com.thesis.eds.utils.DialogUtils


class DiagnosticResultFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDiagnosticResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DiagnosticResultViewModel>()
    private val args : DiagnosticResultFragmentArgs by navArgs()
    private var uri : Uri? = null
    private var finalResult : String? = null
    private var onBackPressedCallback: OnBackPressedCallback? = null

    private var unsavedChanges = false
//    private val onBackPressedCallback = object : OnBackPressedCallback(true){
//        override fun handleOnBackPressed() {
//            showExitAlertDialog()
//        }
//    }

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

        showBottomSheetDialog(uri!!)

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

        unsavedChanges = true
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        // Set up the back button callback
//        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            showExitAlertDialog(requireContext())
//        }
        // This will ensure that the callback is only enabled when the fragment is at the top of the back stack
//        onBackPressedCallback?.isEnabled = true

//        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//
//            Log.d(ContentValues.TAG, "Is the toolbar clicklistener calledd??????  on the createvieeww ================================== and the toolbar = ${requireContext()}")
//            showExitAlertDialog()
//        }.apply {
//            isEnabled = true
//        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(ContentValues.TAG, " 11ini back pressed fri onCreat kepanggil ga sih               ----------------------------------------------")
                    showExitAlertDialog()
//                if (showExitAlertDialog()){
//                    Log.d(ContentValues.TAG, " 22 ini back pressed fri onCreat kepanggil ga sih               ----------------------------------------------")
//
////                    requireActivity().onBackPressed()
//                    findNavController().navigateUp()
//
//                }

//                if (unsavedChanges) {
//                    // Show warning to user
//                    // ...
//                    showExitAlertDialog()
//                } else {
//                    isEnabled = false
//                    requireActivity().onBackPressed()
//                }
            }
        }.apply { isEnabled = true }
        )



//        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
//        toolbar.setNavigationOnClickListener{
//            Log.d(ContentValues.TAG, " that shit is bussin bussin fr fr my lord these fires are enuiqiovantly fuckin bussin               ----------------------------------------------")
//            findNavController().navigateUp()
////            showExitAlertDialog()
//        }
//        if (findNavController().navigateUp()){
//            showExitAlertDialog()
//        }
//        backButtonPressed()

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
//            R.id.toolbar -> {
//                Log.d(ContentValues.TAG, "toolbal ke klik ga seeehhh             !!!  ----------------------------------------------")
//
//                showExitAlertDialog()
//            }
        }
//        val getSupportFragManager = childFragmentManager
//        val fragment = getSupportFragManager.findFragmentById(R.id.nav_home)?.childFragmentManager?.fragments?.get(0)
//        if (fragment is HostFragment) {
//            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                binding.drawerLayout.closeDrawer(GravityCompat.START, true)
//            } else {
//                binding.drawerLayout.openDrawer(GravityCompat.START, true)
//            }
//        } else {
//            navController.navigateUp()
//        }
    }


    private fun showBottomSheetDialog(uri: Uri) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        Log.d(ContentValues.TAG, "what is the uri to bottomsheeet ?? = $uri               ----------------------------------------------")
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
                    .load(uri)
                    .transform(RoundedCorners(20))
                    .into(resultImg)

                yesButton.setOnClickListener{
//                    val action = DiagnosticFragmentDirections.actionNavDiagnosticToDiagnosticResultFragment(uri.toString())
//                    findNavController().navigate(action)
                    bottomSheetDialog.dismiss()
                }

                noButton.setOnClickListener{
//                    val action = DiagnosticResultFragmentDirections.actionDiagnosticResultFragmentToNavDiagnostic()
//                    findNavController().navigate(action)
                    requireActivity().onBackPressed()
                    bottomSheetDialog.dismiss()
                }

                it2.setOnKeyListener { _, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
//                        val action = DiagnosticResultFragmentDirections.actionDiagnosticResultFragmentToNavDiagnostic()
//                        findNavController().navigate(action)
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

    private fun backButtonPressed(){
//        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
//        toolbar?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
//        toolbar?.setNavigationOnClickListener() {
//            Log.d(ContentValues.TAG, "Is the toolbar clicklistener calledd?????? ================================== and the toolbar = $toolbar | and context ${requireContext()}")
//            showExitAlertDialog()
//        }

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        if (findNavController().currentDestination?.id == R.id.diagnosticResultFragment) {
            toolbar?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
            toolbar?.setNavigationOnClickListener() {
//                Log.d(ContentValues.TAG, "Is the toolbar clicklistener called?????? ================================== and the toolbar = $toolbar | and context ${requireContext()}")
                showExitAlertDialog()
            }
        }
    }

    private fun showExitAlertDialog(){
//        var isGoBack = true
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Data akan hilang. Yakin ingin kembali?")
            .setCancelable(false)
            .setPositiveButton("Iya") { _, _ ->
                // Go back to the previous fragment and lose the current picture data
//                val action = DiagnosticResultFragmentDirections.actionDiagnosticResultFragmentToNavDiagnostic()
//                findNavController().navigate(action)
//                moveBackToDiagFrag()
//                super.requireActivity().onBackPressed()
//                isGoBack = true
//                dialog.dismiss()
                findNavController().navigateUp()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
//                isGoBack = false
                dialog.dismiss()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
//        Log.d(ContentValues.TAG, "nilai isBack apaseh $isGoBack ================================== ")

//        return isGoBack
    }

//    private fun moveBackToDiagFrag(){
//        val action = DiagnosticResultFragmentDirections.actionDiagnosticResultFragmentToNavDiagnostic()
//        findNavController().navigate(action)
//
//    }

//    @Deprecated("Deprecated in Java")
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                val navController = findNavController()
//                Log.d(ContentValues.TAG, "current navcont = ${navController.currentDestination?.id}          !!!  ---------------------------------------------- r.id diagresult frag? ${R.id.diagnosticResultFragment}")
//                if (navController.currentDestination?.id == R.id.diagnosticResultFragment) {
//                    DialogUtils.showExitAlertDialog(requireContext()) {
//                        Log.d(ContentValues.TAG, "dialog upil di kepanggil ga sihh             !!!  ----------------------------------------------")
//
//                        // Go back to the previous fragment and lose the current picture data
//                        findNavController().navigateUp()
//                    }
//                    return true
//                }
//            }
//        }
//        return super.onOptionsItemSelected(item)
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

//    override fun onResume() {
//        super.onResume()
//
//        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            showExitAlertDialog(requireContext())
//        }
//        // This will ensure that the callback is only enabled when the fragment is at the top of the back stack
//        callback.isEnabled = true
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.koreksi_diagnosa))
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
//        if (findNavController().currentDestination?.id == R.id.diagnosticResultFragment) {
//            toolbar.navigationIcon = null
//            toolbar.setNavigationOnClickListener(null)
//        }
        _binding = null
    }


}