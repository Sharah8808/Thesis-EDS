package com.thesis.eds.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentDiagnosticDetailBinding
import com.thesis.eds.databinding.FragmentEditProfileBinding
import com.thesis.eds.ui.viewModels.EditProfileViewModel

class EditProfileFragment : Fragment() {

    private val viewModel by viewModels<EditProfileViewModel>()
    private var _binding : FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        nameEditText = view.findViewById(R.id.edit_profile_name_edit_text)
        emailEditText = view.findViewById(R.id.edit_profile_email_edit_text)
        saveButton = view.findViewById(R.id.edit_profile_save_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        editText.setText("some string")

        binding.etFullname

        // Observe changes to the user's data in the ViewModel
        viewModel.user.observe(viewLifecycleOwner, { user ->
            nameEditText.setText(user.name)
            emailEditText.setText(user.email)
        })

        // Save changes when the save button is clicked
        saveButton.setOnClickListener {
            val newName = nameEditText.text.toString()
            val newEmail = emailEditText.text.toString()

            viewModel.updateUserData(newName, newEmail)

            // Navigate back to the previous screen
            findNavController().popBackStack()
        }
    }
}
