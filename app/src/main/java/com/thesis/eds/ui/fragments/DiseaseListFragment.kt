package com.thesis.eds.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.eds.R
import com.thesis.eds.adapters.DiseaseListAdapter
import com.thesis.eds.data.model.DiseaseList
import com.thesis.eds.databinding.FragmentDiseaseListBinding
import com.thesis.eds.ui.viewModels.DiseaseListViewModel
import com.thesis.eds.utils.interfaces.ActionBarTitleSetter
import com.thesis.eds.utils.interfaces.MenuItemHighlighter

class DiseaseListFragment : Fragment() {
    private var _binding: FragmentDiseaseListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel : DiseaseListViewModel

    private val listDisease = ArrayList<DiseaseList>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiseaseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[DiseaseListViewModel::class.java]
        val diseaseListEntity = viewModel.getDiseaseList()

        val rvDiseaseListAdapter = DiseaseListAdapter(listDisease)
        rvDiseaseListAdapter.setRVDataList(diseaseListEntity)

        with(binding.recyclerviewDiseaseList){
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = rvDiseaseListAdapter
        }
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