package com.thesis.eds.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.thesis.eds.R
import com.thesis.eds.adapters.DiseaseListAdapter
import com.thesis.eds.adapters.HomeHistoryAdapter
import com.thesis.eds.data.model.DiseaseList
import com.thesis.eds.data.model.HistoryDb
import com.thesis.eds.databinding.FragmentHomeBinding
import com.thesis.eds.ui.viewModels.HomeViewModel

class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private val listHistory = mutableListOf<HistoryDb>()
    private val listDisease = ArrayList<DiseaseList>()
    private lateinit var firebaseAuth: FirebaseAuth
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            Log.d("EDSThesis_HomeFrag", "Permission is not granted.")
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION
            )
        } else {
            // Permission is already granted, do your stuff
            Log.d("EDSThesis_HomeFrag", "Permission is granted.")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadUserData()
        firebaseAuth = FirebaseAuth.getInstance()

        val btnCategory: Button = view.findViewById(binding.buttonDiagnostic.id)
        val textHistoryAll : TextView = view.findViewById(binding.textHistorySeeAll.id)
        val textDiseaseListAll : TextView = view.findViewById(binding.textDiseaseListSeeAll.id)
        btnCategory.setOnClickListener(this)
        textHistoryAll.setOnClickListener(this)
        textDiseaseListAll.setOnClickListener(this)

        greetingsWidget()
        showAllRecyclerView()
    }

    private fun showAllRecyclerView() {
        Log.d("EDSThesis_HomeFrag", "Showing home history and disease list recycler view widget....")

        val adapterHistory = HomeHistoryAdapter(listHistory)
        binding.homeScrollHistory.adapter = adapterHistory
        binding.homeScrollHistory.layoutManager = LinearLayoutManager(requireContext())
        viewModel.historyList.observe(viewLifecycleOwner) { historyList ->
            adapterHistory.setRVDataList(historyList)
        }
        viewModel.getHistoryList()

        val diseaseListEntity = viewModel.getDiseaseList()
        val rvDiseaseListAdapter = DiseaseListAdapter(listDisease)
        rvDiseaseListAdapter.setRVDataList(diseaseListEntity)
        with(binding.homeScrollDiseaseList){
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = rvDiseaseListAdapter
        }
    }
        override fun onClick(v: View) {
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

    private fun greetingsWidget(){
        viewModel.formatDate()
        viewModel.dateTime.observe(viewLifecycleOwner){ time ->
            binding.textDate.text = time
        }
        binding.textGreetingsDayTime.text = viewModel.getGreeting()
        viewModel.user.observe(viewLifecycleOwner){ user ->
            binding.textGreetingsUsername.text = user?.fullname
            val profilePictureUrl = user?.img

            if (profilePictureUrl != null) {
                Glide.with(this)
                    .load(profilePictureUrl)
                    .into(binding.imgAvatar)
            } else {
                // Use a default image if the user doesn't have a profile picture
                Glide.with(this)
                    .load(R.drawable.chawieputh)
                    .into(binding.imgAvatar)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}