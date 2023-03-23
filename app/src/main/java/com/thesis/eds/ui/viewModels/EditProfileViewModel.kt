package com.thesis.eds.ui.viewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.eds.data.model.User
import com.thesis.eds.data.repository.UserRepository

class EditProfileViewModel : ViewModel() {

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
                Log.d(TAG, "Error getting user data: ", e)
            }
    }

    fun updateUserData(newName: String, newEmail: String) {
        val currentUser = userRepository.getCurrentUser()

        val userData = mapOf(
            "name" to newName,
            "email" to newEmail
        )

        userRepository.updateUserData(currentUser.uid, userData)
            .addOnSuccessListener {
                Log.d(TAG, "User data updated successfully!")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error updating user data: ", e)
            }
    }
}
