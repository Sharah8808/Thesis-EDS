package com.thesis.eds.ui.viewModels

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.eds.data.model.DiseaseList
import com.thesis.eds.data.model.History
import com.thesis.eds.data.model.User
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
                Log.d(ContentValues.TAG, "Error getting user data: ", e)
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

    fun getHistoryList():List<History> = Dummy.getDummyHistory()

fun getDiseaseList(): List<DiseaseList> = Dummy.getDummyDiseaseList()

}