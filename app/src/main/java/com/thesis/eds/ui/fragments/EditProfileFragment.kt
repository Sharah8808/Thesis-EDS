package com.thesis.eds.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thesis.eds.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private val binding get() = _binding!!
    private var _binding: FragmentEditProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


}