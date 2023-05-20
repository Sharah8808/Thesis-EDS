package com.thesis.eds.ui.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.eds.data.model.User
import com.thesis.eds.data.repository.UserRepository

class SettingViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?>
        get() = _user

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
                Log.d("EDSThesis_Setting", "Error getting user data: ", e)
            }
    }
}