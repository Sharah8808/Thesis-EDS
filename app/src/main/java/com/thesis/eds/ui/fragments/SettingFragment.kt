package com.thesis.eds.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentSettingBinding
import com.thesis.eds.ui.viewModels.SettingViewModel
import com.thesis.eds.interfaces.ActionBarTitleSetter
import com.thesis.eds.interfaces.MenuItemHighlighter

class SettingFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentSettingBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingViewModel =
            ViewModelProvider(this).get(SettingViewModel::class.java)

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
//        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
//        val logout = binding.textLogout.setOnClickListener {
//            firebaseAuth.signOut()
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val logout : TextView = view.findViewById(binding.textLogout.id)
        val editProfile : Button = view.findViewById(binding.buttonEditProfile.id)

        logout.setOnClickListener(this)
        editProfile.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        Toast.makeText(requireActivity(), "is setting oncliked cliked??", Toast.LENGTH_SHORT).show()
        when(v?.id){
            binding.textLogout.id -> {
                firebaseAuth.signOut()
                val action = SettingFragmentDirections.actionNavSettingToLoginActivity()
                findNavController().navigate(action)
            }
            binding.buttonEditProfile.id -> {
                Toast.makeText(requireActivity(), "is button edit profile cliked???", Toast.LENGTH_SHORT).show()
                val action = SettingFragmentDirections.actionNavSettingToEditProfileFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        Toast.makeText(requireActivity(), "is kierooo heerre?", Toast.LENGTH_SHORT).show()
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_pengaturan))
    }


}