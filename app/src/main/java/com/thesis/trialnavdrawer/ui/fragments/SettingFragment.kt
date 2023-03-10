package com.thesis.trialnavdrawer.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.thesis.trialnavdrawer.R
import com.thesis.trialnavdrawer.databinding.FragmentSettingBinding
import com.thesis.trialnavdrawer.ui.viewModels.SettingViewModel
import com.thesis.trialnavdrawer.interfaces.ActionBarTitleSetter
import com.thesis.trialnavdrawer.interfaces.MenuItemHighlighter

class SettingFragment : Fragment() {
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
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        val logout = binding.textLogout.setOnClickListener {
            firebaseAuth.signOut()
        }
//        val textView: TextView = binding.textSetting
//        settingViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        Toast.makeText(requireActivity(), "is kierooo heerre?", Toast.LENGTH_SHORT).show()
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_pengaturan))
        (activity as MenuItemHighlighter).setMenuHighlight(4)
    }
}