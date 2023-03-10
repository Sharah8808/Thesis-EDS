package com.thesis.trialnavdrawer.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.thesis.trialnavdrawer.R
import com.thesis.trialnavdrawer.databinding.FragmentDiagnosticBinding
import com.thesis.trialnavdrawer.ui.viewModels.DiagnosticViewModel
import com.thesis.trialnavdrawer.interfaces.ActionBarTitleSetter
import com.thesis.trialnavdrawer.interfaces.MenuItemHighlighter

class DiagnosticFragment : Fragment() {

    private var _binding: FragmentDiagnosticBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val diagnosticViewModel =
            ViewModelProvider(this).get(DiagnosticViewModel::class.java)

        _binding = FragmentDiagnosticBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Toast.makeText(requireActivity(), "hoopplaaaa", Toast.LENGTH_SHORT).show()
//        val textView: TextView = binding.textDiagnostic
//        diagnosticViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        replaceFragment(this)
//        val btnCategory: Button = view.findViewById(binding.buttonDiagnostic.id)
//        btnCategory.setOnClickListener(this)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_diagnostic, fragment)
        transaction.commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        Toast.makeText(requireActivity(), "is kierooo heerre?", Toast.LENGTH_SHORT).show()
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_diagnosa))
        (activity as MenuItemHighlighter).setMenuHighlight(1)
    }
}