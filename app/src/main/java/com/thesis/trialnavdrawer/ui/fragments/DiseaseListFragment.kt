package com.thesis.trialnavdrawer.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.thesis.trialnavdrawer.R
import com.thesis.trialnavdrawer.databinding.FragmentDiseaseListBinding
import com.thesis.trialnavdrawer.ui.viewModels.DiseaseListViewModel
import com.thesis.trialnavdrawer.interfaces.ActionBarTitleSetter
import com.thesis.trialnavdrawer.interfaces.MenuItemHighlighter

class DiseaseListFragment : Fragment() {
    private var _binding: FragmentDiseaseListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val diseaseListViewModel =
            ViewModelProvider(this).get(DiseaseListViewModel::class.java)

        _binding = FragmentDiseaseListBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDiseaseList
//        diseaseListViewModel.text.observe(viewLifecycleOwner) {
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
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_daftar))
        (activity as MenuItemHighlighter).setMenuHighlight(3)
    }
}