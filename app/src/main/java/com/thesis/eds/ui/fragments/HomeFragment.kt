package com.thesis.eds.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.thesis.eds.R
import com.thesis.eds.adapters.DiseaseListAdapter
import com.thesis.eds.adapters.HistoryAdapter
import com.thesis.eds.adapters.HomeHistoryAdapter
import com.thesis.eds.data.model.DiseaseList
import com.thesis.eds.data.model.History
import com.thesis.eds.data.model.HistoryDb
import com.thesis.eds.databinding.FragmentHomeBinding
import com.thesis.eds.ui.viewModels.EditProfileViewModel
import com.thesis.eds.ui.viewModels.HomeViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
//    private val listHistory = List<HistoryDb>()
    private val listHistory = mutableListOf<HistoryDb>()
    private val listDisease = ArrayList<DiseaseList>()
    private lateinit var firebaseAuth: FirebaseAuth
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 101

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!! permission granteeeed")

                // Permission is granted, do your stuff
            } else {
                Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!! permission not granteeedd")

                // Permission denied, handle accordingly
            }
        }

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
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION
            )
        } else {
            // Permission is already granted, do your stuff
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

//        val historyEntity = viewModel.getHistoryList()
//        val rvhistoryAdapter = HomeHistoryAdapter(listHistory)
//        rvhistoryAdapter.setRVDataList(historyEntity)
//        with(binding.homeScrollHistory){
//            layoutManager = LinearLayoutManager(context)
//            setHasFixedSize(true)
//            adapter = rvhistoryAdapter
//        }
//
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