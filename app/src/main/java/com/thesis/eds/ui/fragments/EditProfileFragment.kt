package com.thesis.eds.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentEditProfileBinding
import com.thesis.eds.interfaces.ActionBarTitleSetter
import com.thesis.eds.ui.viewModels.EditProfileViewModel

class EditProfileFragment : Fragment() {

    private val viewModel by viewModels<EditProfileViewModel>()
    private var _binding : FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var selectImageLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val toolbar: Toolbar? = root.findViewById(R.id.toolbar)
        toolbar?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
        toolbar?.setNavigationOnClickListener { requireActivity().onBackPressed() }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadUserData()

//      Observe changes to the user's data in the ViewModel
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.etFullname.setText(user?.fullname)
            binding.etEmail.setText(user?.email)
            binding.etPhoneNumber.setText(user?.phoneNumber)
        }

        // Initialize the ActivityResultLaunchers
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                // Update the CircleImageView with the taken picture
                binding.imgAvatar.setImageURI(viewModel.imageUri)
            }
        }

        selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                // Update the CircleImageView with the selected image
                binding.imgAvatar.setImageURI(uri)
                viewModel.imageUri = uri
            }
        }

        // Set an OnClickListener to the CircleImageView
        binding.imgAvatar.setOnClickListener {
            // Show a dialog with options to take a picture or select an image from the gallery
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
                        takePictureLauncher.launch(viewModel.createImageUri(requireContext()))
                    }
                    options[item] == "Choose from Gallery" -> {
                        selectImageLauncher.launch("image/*")
                    }
                    options[item] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }

        // Save changes when the save button is clicked
        binding.buttonSave.setOnClickListener {
            val newName = binding.etFullname.text.toString()
            val newEmail = binding.etEmail.text.toString()
            val newPhoneNumber = binding.etPhoneNumber.text.toString()
            val newPassword = binding.etNewPassword.text.toString()
            val oldPassword = binding.etOldPassword.text.toString()

            viewModel.updateUserData(newName, newEmail, newPhoneNumber, newPassword, oldPassword)

            // Navigate back to the previous screen
            findNavController().popBackStack()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_edit_profil))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
