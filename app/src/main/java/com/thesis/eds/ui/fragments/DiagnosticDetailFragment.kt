package com.thesis.eds.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.media.MediaScannerConnection
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
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentDiagnosticDetailBinding
import com.thesis.eds.ui.viewModels.DiagnosticDetailViewModel
import com.thesis.eds.utils.interfaces.ActionBarTitleSetter

class DiagnosticDetailFragment : Fragment() {

    private var _binding : FragmentDiagnosticDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DiagnosticDetailViewModel
    private val args : DiagnosticDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDiagnosticDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[DiagnosticDetailViewModel::class.java]
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

    private fun showEditOptions(){
        val options = arrayOf<CharSequence>("Ubah nama", "Unduh gambar", "Hapus riwayat")
        val builder = AlertDialog.Builder(requireContext())
        val timeStamp = args.historyTimeStamp
        builder.setTitle("Pilih menu")
        builder.setItems(options){ _, item ->
            when{
                options[item] == "Ubah nama" -> {
                    showEditNameDialog { newHistoryName ->
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
                                Log.e("EDSThesis_DDetailFrag", "Error downloading image", e)
                            }
                        )
                    }
                }
                options[item] == "Hapus riwayat" -> {
                    Toast.makeText(requireActivity(), "Riwayat berhasil di hapus", Toast.LENGTH_SHORT).show()
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
            null
        ) { path, _ -> Log.d("EDSThesis_DDetailFrag", "Scanned $path ----------") }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_detail))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}