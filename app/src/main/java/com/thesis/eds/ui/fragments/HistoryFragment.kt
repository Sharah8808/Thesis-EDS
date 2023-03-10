package com.thesis.eds.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thesis.eds.R
import com.thesis.eds.adapters.HistoryAdapter
import com.thesis.eds.data.History
import com.thesis.eds.databinding.FragmentHistoryBinding
import com.thesis.eds.ui.viewModels.HistoryViewModel
import com.thesis.eds.interfaces.ActionBarTitleSetter
import com.thesis.eds.interfaces.MenuItemHighlighter
import com.thesis.eds.interfaces.RecyclerViewClickListener

class HistoryFragment : Fragment(), RecyclerViewClickListener {

    private val binding get() = _binding!!
    private var _binding: FragmentHistoryBinding? = null
    private lateinit var rvHistory : RecyclerView
    private val list = ArrayList<History>()
//    private lateinit var rvHistoryAdapter: HistoryAdapter
    private lateinit var viewModel : HistoryViewModel
//    private val navControl = findNavController()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[HistoryViewModel::class.java]
        val historyEntity = viewModel.getHistoryList()

         val rvHistoryAdapter = HistoryAdapter(this@HistoryFragment)
        rvHistoryAdapter.setRVDataList(historyEntity)
//        rvHistoryAdapter.listener = this

//        rvHistory = binding.recyclerviewHistory
//        rvHistory.setHasFixedSize(true)
        with(binding.recyclerviewHistory) {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = rvHistoryAdapter
        }

//        list.addAll(getListHistory())
//        showRecyclerList()
//        clickUserList(rvHistoryAdapter)

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
//        navControl.navigate(R)
        val mCategoryFragment = DiagnosticDetailFragment()
        val mBundle = Bundle()
        mBundle.putInt(DiagnosticDetailFragment.EXTRA_DATA, historyEntity.id_history!!)
        mCategoryFragment.arguments = mBundle
        val mFragmentManager = parentFragmentManager
        mFragmentManager.beginTransaction().apply {
            replace(
                binding.frameHistoryFragment.id,
                mCategoryFragment,
                DiagnosticDetailFragment::class.java.simpleName
            )
            addToBackStack(null)
            setReorderingAllowed(true)
            commit()

        }


//        startActivity(Intent(context, DetailActivity::class.java)
//            .putExtra(DetailActivity.EXTRA_DATA, entity.id)
//            .putExtra(DetailActivity.EXTRA_TYPE, TYPE_MOVIE))
    }


//    private fun clickUserList(adapter: HistoryAdapter) {
//        Toast.makeText(requireActivity(), "apakah terpanggil", Toast.LENGTH_SHORT).show()
//        adapter.setOnItemClickCallback(object :
//            HistoryAdapter.OnItemClickCallback {
//            override fun onItemClicked(history: History) {
////                val rvDetailIntent = Intent(activity, RVDetailActivity::class.java)
////                rvDetailIntent.putExtra(RVDetailActivity.EXTRA_BUNDLE, data)
////                startActivity(rvDetailIntent)
//                Toast.makeText(requireActivity(), "test pindah ke detail riwayat", Toast.LENGTH_SHORT).show()
//                val mCategoryFragment = DiagnosticDetailFragment()
//                val mFragmentManager = parentFragmentManager
//                mFragmentManager.beginTransaction().apply {
//                    replace(
//                        binding.frameHistoryFragment.id,
//                        mCategoryFragment,
//                        DiagnosticDetailFragment::class.java.simpleName
//                    )
////                    binding.appBarLayout.setExpanded(true, true)
//                    addToBackStack(null)
//                    commit()
//                }
//            } })
//    }



//    override fun onItemClicked(entity: History) {
////        rvHistoryAdapter.listener.onItemClicked()
//
//        val mCategoryFragment = DiagnosticDetailFragment()
//        val mFragmentManager = parentFragmentManager
//        mFragmentManager.beginTransaction().apply {
//            Toast.makeText(requireActivity(), "history to detail?", Toast.LENGTH_SHORT).show()
//            replace(
//                binding.frameHistoryFragment.id,
//                mCategoryFragment,
//                DiagnosticDetailFragment::class.java.simpleName
//            )
////                    binding.appBarLayout.setExpanded(true, true)
//            addToBackStack(null)
//            commit()
//        }
//    }
}