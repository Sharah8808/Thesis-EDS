package com.thesis.eds.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thesis.eds.R
import com.thesis.eds.adapters.DiseaseListAdapter
import com.thesis.eds.adapters.HomeHistoryAdapter
import com.thesis.eds.data.DiseaseList
import com.thesis.eds.data.History
import com.thesis.eds.databinding.FragmentHomeBinding
import com.thesis.eds.ui.viewModels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : HomeViewModel
    private lateinit var rvHistory : RecyclerView
    private val listHistory = ArrayList<History>()
    private val listDisease = ArrayList<DiseaseList>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDate
        dateApplicator(textView)

        return root
    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val btnCategory: Button = view.findViewById(binding.buttonDiagnostic.id)
            val textHistoryAll : TextView = view.findViewById(binding.textHistorySeeAll.id)
            val textDiseaseListAll : TextView = view.findViewById(binding.textDiseaseListSeeAll.id)
            btnCategory.setOnClickListener(this)
            textHistoryAll.setOnClickListener(this)
            textDiseaseListAll.setOnClickListener(this)

            viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[HomeViewModel::class.java]
            val historyEntity = viewModel.getHistoryList()
            val diseaseListEntity = viewModel.getDiseaseList()

            val rvhistoryAdapter = HomeHistoryAdapter(listHistory)
            val rvDiseaseListAdapter = DiseaseListAdapter(listDisease)
            rvhistoryAdapter.setRVDataList(historyEntity)
            rvDiseaseListAdapter.setRVDataList(diseaseListEntity)

            with(binding.homeScrollHistory){
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = rvhistoryAdapter
            }

            with(binding.homeScrollDiseaseList){
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = rvDiseaseListAdapter
            }

        }

        override fun onClick(v: View) {
            Toast.makeText(requireActivity(), "teessst", Toast.LENGTH_SHORT).show()
            when (v.id) {
                R.id.button_diagnostic -> {
                    val action = HomeFragmentDirections.actionNavHomeToNavDiagnostic()
                    findNavController().navigate(action)
                }

                R.id.text_history_see_all -> {
                    val action = HomeFragmentDirections.actionNavHomeToNavHistory()
                    findNavController().navigate(action)
                }

                R.id.text_disease_list_see_all -> {
                    val action = HomeFragmentDirections.actionNavHomeToNavDiseaseList()
                    findNavController().navigate(action)
                }
            }
        }

    @SuppressLint("SimpleDateFormat")
    fun dateApplicator(textView: TextView){
        val dateTime : String
        val calendar : Calendar
        val simpleDateFormat : SimpleDateFormat

        calendar = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("EEEE, dd LLLL yyyy")
        dateTime = simpleDateFormat.format(calendar.time).toString()
        textView.text = dateTime
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}