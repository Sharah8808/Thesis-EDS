package com.thesis.eds.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thesis.eds.R
import com.thesis.eds.adapters.HistoryAdapter
import com.thesis.eds.data.model.History
import com.thesis.eds.databinding.FragmentHistoryBinding
import com.thesis.eds.ui.viewModels.HistoryViewModel
import com.thesis.eds.interfaces.ActionBarTitleSetter
import com.thesis.eds.interfaces.MenuItemHighlighter
import com.thesis.eds.interfaces.RecyclerViewClickListener

class HistoryFragment : Fragment(), RecyclerViewClickListener {

    private val binding get() = _binding!!
    private var _binding: FragmentHistoryBinding? = null
    private lateinit var viewModel : HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[HistoryViewModel::class.java]
        val historyEntity = viewModel.getHistoryList()

         val rvHistoryAdapter = HistoryAdapter(this@HistoryFragment)
        rvHistoryAdapter.setRVDataList(historyEntity)

        with(binding.recyclerviewHistory) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = rvHistoryAdapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        Toast.makeText(requireActivity(), "is kierooo heerre?", Toast.LENGTH_SHORT).show()
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_riwayat))
        (activity as MenuItemHighlighter).setMenuHighlight(2)
    }

    override fun onItemClicked(historyEntity: History) {
        Toast.makeText(requireActivity(), "test pindah ke detailleee", Toast.LENGTH_SHORT).show()

        val hisId = historyEntity.id_history
        val action = HistoryFragmentDirections.actionNavHistoryToNavDiagDetail(hisId!!)
        findNavController().navigate(action )

    }
}