package com.thesis.eds.ui.viewModels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.thesis.eds.data.model.DiseaseList
import com.thesis.eds.data.model.HistoryDb
import com.thesis.eds.data.model.User
import com.thesis.eds.data.repository.HistoryRepository
import com.thesis.eds.data.repository.UserRepository
import com.thesis.eds.utils.Dummy
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?>
        get() = _user

    private var _dateTime = MutableLiveData<String>()
    val dateTime: LiveData<String>
        get() = _dateTime

    private val _historyList = MutableLiveData<List<HistoryDb>>()
    val historyList: LiveData<List<HistoryDb>> = _historyList
    private val historyRepository = HistoryRepository()

    private val MORNING_START_HOUR = 5
    private val AFTERNOON_START_HOUR = 12
    private val EVENING_START_HOUR = 16
    private val NIGHT_START_HOUR = 19

    fun loadUserData() {
        val currentUser = userRepository.getCurrentUser()

        userRepository.getUserData(currentUser.uid)
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    _user.value = user
                }
            }
            .addOnFailureListener { e ->
                Log.d("EDSThesis_HomeVM", "Showing home history and disease list recycler view widget....")
            }
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate() {
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("EEEE, dd LLLL yyyy")
        _dateTime.value = simpleDateFormat.format(calendar.time)
    }

    fun getGreeting(): String {
        val calendar = Calendar.getInstance()
        return when(calendar.get(Calendar.HOUR_OF_DAY)) {
            in MORNING_START_HOUR until AFTERNOON_START_HOUR -> "Selamat Pagi, "
            in AFTERNOON_START_HOUR until EVENING_START_HOUR -> "Selamat Siang, "
            in EVENING_START_HOUR until NIGHT_START_HOUR -> "Selamat Sore, "
            else -> "Selamat Malam, "
        }
    }

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

    fun getDiseaseList(): List<DiseaseList> = Dummy.getDummyDiseaseList()
}