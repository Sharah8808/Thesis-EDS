package com.thesis.eds.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.eds.R
import com.thesis.eds.adapters.HistoryAdapter
import com.thesis.eds.data.model.HistoryDb
import com.thesis.eds.data.repository.HistoryRepository
import com.thesis.eds.databinding.FragmentHistoryBinding
import com.thesis.eds.ui.modelFactories.HistoryViewModelFactory
import com.thesis.eds.ui.viewModels.HistoryViewModel
import com.thesis.eds.utils.interfaces.ActionBarTitleSetter
import com.thesis.eds.utils.interfaces.MenuItemHighlighter
import com.thesis.eds.utils.interfaces.RecyclerViewClickListener

class HistoryFragment : Fragment(), RecyclerViewClickListener {

    private val binding get() = _binding!!
    private var _binding: FragmentHistoryBinding? = null
    private lateinit var viewModel : HistoryViewModel
    private lateinit var adapter: HistoryAdapter

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

        adapter = HistoryAdapter(this)
        binding.recyclerviewHistory.adapter = adapter
        binding.recyclerviewHistory.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(this,
            HistoryViewModelFactory(HistoryRepository()))[HistoryViewModel::class.java]

        viewModel.historyList.observe(viewLifecycleOwner) { historyList ->
            adapter.setRVDataList(historyList)
        }
        viewModel.getHistoryList()

        initSearchView()
    }

    override fun onItemClicked(historyEntity: HistoryDb) {
        Log.d("EDSThesis_History", "Move to detail history...")

        val userId = historyEntity.timeStamp
        val action = HistoryFragmentDirections.actionNavHistoryToNavDiagDetail(userId)
        findNavController().navigate(action)
    }

    private fun initSearchView() {
        binding.searchviewHistory.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = viewModel.searchHistoryByName(newText.orEmpty())
                adapter.setRVDataList(filteredList)
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        Log.d("EDSThesis_History", "Currently on History Fragment...")
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_riwayat))
        (activity as MenuItemHighlighter).setMenuHighlight(2)
    }

}