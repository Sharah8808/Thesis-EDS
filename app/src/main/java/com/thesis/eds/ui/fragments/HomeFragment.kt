package com.thesis.eds.ui.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.thesis.eds.R
import com.thesis.eds.adapters.DiseaseListAdapter
import com.thesis.eds.adapters.HomeHistoryAdapter
import com.thesis.eds.data.model.DiseaseList
import com.thesis.eds.data.model.History
import com.thesis.eds.databinding.FragmentHomeBinding
import com.thesis.eds.ui.viewModels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : HomeViewModel
    private lateinit var rvHistory : RecyclerView
    private val listHistory = ArrayList<History>()
    private val listDisease = ArrayList<DiseaseList>()

    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore
//    private lateinit var database: DatabaseReference
//    companion object

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[HomeViewModel::class.java]
            firebaseAuth = FirebaseAuth.getInstance()
//            database = Firebase.database.reference

            val textView: TextView = binding.textDate
            val currentDayTime = dateApplicator(textView) + " "
            greetings(currentDayTime)

            val btnCategory: Button = view.findViewById(binding.buttonDiagnostic.id)
            val textHistoryAll : TextView = view.findViewById(binding.textHistorySeeAll.id)
            val textDiseaseListAll : TextView = view.findViewById(binding.textDiseaseListSeeAll.id)
            btnCategory.setOnClickListener(this)
            textHistoryAll.setOnClickListener(this)
            textDiseaseListAll.setOnClickListener(this)

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
//            Toast.makeText(requireActivity(), "teessst", Toast.LENGTH_SHORT).show()
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
    fun dateApplicator(textView: TextView) : String{
        val dateTime : String
        val calendar : Calendar
        val simpleDateFormat : SimpleDateFormat

        calendar = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("EEEE, dd LLLL yyyy")
        dateTime = simpleDateFormat.format(calendar.time).toString()
        textView.text = dateTime

        val currentDay = when(calendar.get(Calendar.HOUR_OF_DAY)){
            in 5 .. 11 -> {
                "Selamat Pagi"
            }
            in 12 .. 15 -> {
                "Selamat Siang"
            }
            in 16 .. 18 -> {
                "Selamat Sore"
            }
            else -> {
                "Selamat Malam"
            }
        }
        return currentDay
    }

    private fun greetings(dayTime : String){
        binding.textGreetingsDayTime.text = dayTime
//        val reference = FirebaseDatabase.getInstance().getReference("users")

        val currUser = firebaseAuth.currentUser
        val uid = currUser?.uid

        // Get a reference to the Firestore collection
        val collectionRef = FirebaseFirestore.getInstance().collection("users")

        // Query the collection to retrieve the user document with a specific ID
        val userDocRef = collectionRef.document(uid!!)

        // Retrieve the user's name field from the document
        userDocRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val userName = doc.getString("username")
                    binding.textGreetingsUsername.text = userName
                    Log.d(TAG, "User's name: $userName")
                } else {
                    Log.d(TAG, "No such document!")
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error getting document: ", e)
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}