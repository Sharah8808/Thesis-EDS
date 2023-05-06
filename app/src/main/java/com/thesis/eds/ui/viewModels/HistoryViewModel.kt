package com.thesis.eds.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.thesis.eds.data.model.History
import com.thesis.eds.data.model.HistoryDb
import com.thesis.eds.data.repository.HistoryRepository
import com.thesis.eds.utils.Dummy

class HistoryViewModel(private val historyRepository : HistoryRepository): ViewModel() {

//    fun getHistoryList():List<History> = Dummy.getDummyHistory()

//    private val historyRepository = HistoryRepository()
    private val _historyList = MutableLiveData<List<HistoryDb>>()
    val historyList: LiveData<List<HistoryDb>> = _historyList

    fun getHistoryList() {
        val currentUser = historyRepository.getCurrentUser()
        val query = historyRepository.getHistoriesCollection()
            .whereEqualTo("userId", currentUser.uid)
            .orderBy("timeStamp", Query.Direction.DESCENDING)

        query.addSnapshotListener { value, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }
            val historyList = mutableListOf<HistoryDb>()
            value?.forEach { documentSnapshot ->
                val history = documentSnapshot.toObject(HistoryDb::class.java)
                historyList.add(history)
            }
            _historyList.value = historyList
        }
    }

    fun searchHistoryByName(query: String): List<HistoryDb> {
        return historyList.value?.filter { history -> history.name.contains(query, ignoreCase = true) } ?: emptyList()
    }

}