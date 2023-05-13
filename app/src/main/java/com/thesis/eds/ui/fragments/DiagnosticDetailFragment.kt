package com.thesis.eds.ui.fragments

//import android.R
import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.thesis.eds.R
import com.thesis.eds.data.model.History
import com.thesis.eds.databinding.FragmentDiagnosticDetailBinding
import com.thesis.eds.utils.interfaces.ActionBarTitleSetter
import com.thesis.eds.ui.viewModels.DiagnosticDetailViewModel

class DiagnosticDetailFragment : Fragment() {

    private var _binding : FragmentDiagnosticDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DiagnosticDetailViewModel

    //    companion object{
//        const val EXTRA_DATA = "extra_data"
//    }
    private val args : DiagnosticDetailFragmentArgs by navArgs()
//    private val timeStamp : String = args.historyTimeStamp

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDiagnosticDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val toolbar: Toolbar? = root.findViewById(R.id.toolbar)
//        toolbar?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
//        toolbar?.setNavigationOnClickListener { requireActivity().onBackPressed() }

//        Toast.makeText(requireActivity(), "haaaloooooooooooooooooo", Toast.LENGTH_SHORT).show()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[DiagnosticDetailViewModel::class.java]
//        fragmentTransactionProcess()

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        val btnCategory: Button = view.findViewById(binding.buttonDiagnostic.id)
//        btnCategory.setOnClickListener(this)
        val timeStamp = args.historyTimeStamp
        viewModel.loadHistoryData(timeStamp)
        observeDataChanges()
        binding.buttonEdit.setOnClickListener{
            showEditOptions()
        }
    }

    private fun observeDataChanges(){
        //Observe changes to the user's data in the ViewModel
        viewModel.history.observe(viewLifecycleOwner) { history ->
            binding.textDetailResult.text = history?.actual_result
            binding.textDetailName.text = history?.name
            binding.textDetailDate.text = history?.timeStamp
            val historyImage = history?.img

            if (historyImage != null) {
                Glide.with(this)
                    .load(historyImage)
                    .into(binding.imgPhotoDetail)
            } else {
                // Use a default image if the user doesn't have a profile picture
                Glide.with(this)
                    .load(R.drawable.chawieputh)
                    .into(binding.imgPhotoDetail)
            }
        }
    }

//    private fun fragmentTransactionProcess(){
//        if(arguments != null){
//            val extra = arguments?.getInt(EXTRA_DATA)
//            extra?.let { viewModel.setSelectedEntityHistory(it) }
//            showDetailHistory(viewModel.selectHistory())
//        }
        //argumen berisi extra data (id), masuk ke val extra, dri extra di ambil id-ny pake it, panggil viewmodel dgn id it, dll dll

//        viewModel.setSelectedEntityHistory(args.historyUserId)
//        showDetailHistory(viewModel.selectHistory())
//    }

    private fun showDetailHistory(history : History){
        binding.textDetailName.text = history.name_history
        binding.textDetailResult.text = history.result_history
        binding.textDetailDate.text = history.date_history
        Glide.with(this)
            .load(history.image_history)
            .transform(RoundedCorners(20))
            .into(binding.imgPhotoDetail)
    }

    override fun onAttach(context: Context) {
//        Toast.makeText(requireActivity(), "is kierooo heerre? from detail detail frag", Toast.LENGTH_SHORT).show()
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_detail))


    //   (activity as MenuItemHighlighter).setMenuHighlight(2)
    }

    private fun showEditOptions(){
        val options = arrayOf<CharSequence>("Ubah nama", "Unduh gambar", "Hapus riwayat")
        val builder = AlertDialog.Builder(requireContext())
        val timeStamp = args.historyTimeStamp
        builder.setTitle("Pilih menu")
        builder.setItems(options){ dialog, item ->
            when{
                options[item] == "Ubah nama" -> {
                    showEditNameDialog { newHistoryName ->
//                        val timeStamp = args.historyTimeStamp
                        viewModel.editHistoryName(newHistoryName, timeStamp)
                    }
                }
                options[item] == "Unduh gambar" -> {
                    viewModel.history.observe(viewLifecycleOwner) { history ->
                        val historyResult = history?.actual_result
                        // do something with historyName
                        viewModel.downloadPicture(timeStamp, historyResult!!,
                            { filePath ->
                                // Image downloaded successfully, do something with the filePath
                                mediaScanner(filePath)
                                Toast.makeText(requireActivity(), "Gambar berhasil di unduh", Toast.LENGTH_SHORT).show()
                            },
                            { e ->
                                // Error downloading image
                                Log.e(TAG, "Error downloading image", e)
                            }
                        )
                    }
//                    viewModel.downloadPicture(timeStamp,
//                        { filePath ->
//                            // Image downloaded successfully, do something with the filePath
//                            mediaScanner(filePath)
//                            Toast.makeText(requireActivity(), "Gambar berhasil di unduh", Toast.LENGTH_SHORT).show()
//                        },
//                        { e ->
//                            // Error downloading image
//                            Log.e(TAG, "Error downloading image", e)
//                        }
//                    )
                }
                options[item] == "Hapus riwayat" -> {
                    showDeleteAlertDialog()
                }
            }
        }
        builder.show()

    }

    private fun showEditNameDialog(callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Tulis nama")

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

    private fun showDeleteAlertDialog(){
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Yakin hapus riwayat?")
            .setCancelable(false)
            .setPositiveButton("Iya") { _, _ ->
                val timeStamp = args.historyTimeStamp
                viewModel.eraseHistory(timeStamp)
                findNavController().navigateUp()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun mediaScanner(filePath: String){
        MediaScannerConnection.scanFile(
            requireContext(),
            arrayOf(filePath),
            null,
            object : MediaScannerConnection.OnScanCompletedListener {
                override fun onScanCompleted(path: String?, uri: Uri?) {
                    Log.d(ContentValues.TAG, "Scanned $path")
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}