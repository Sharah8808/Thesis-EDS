package com.thesis.eds.ui.viewModels

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.eds.data.model.User
import com.thesis.eds.data.repository.UserRepository
import java.io.File

class EditProfileViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?>
        get() = _user

    var imageUri: Uri? = null

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

    fun updateUserData(newName: String, newEmail: String, newPhoneNumber: String, newPassword: String?, oldPassword: String?) {
        val currentUser = userRepository.getCurrentUser()

        if (newPassword.isNullOrBlank() && oldPassword.isNullOrBlank()) {
            // Do not update password
            updateUserDataWithoutPassword(currentUser.uid, newName, newEmail, newPhoneNumber)
        } else {
            // Check if old password matches current user's password
            oldPassword?.let {
                userRepository.checkPassword(currentUser.uid, it) { isPasswordCorrect ->
                    if (isPasswordCorrect) {
                        // Update password
                        newPassword?.let { it1 ->
                            updateUserDataWithPassword(currentUser.uid, newName, newEmail, newPhoneNumber,
                                it1
                            )
                        }
                    } else {
                        // Display error message to the user
                        Log.d(TAG, "Error update user data, password isnt correct")
                    }
                }
            }
        }
    }

    private fun updateUserDataWithoutPassword(uid: String, newName: String, newEmail: String, newPhoneNumber: String) {
        val userData = mapOf(
            "fullname" to newName,
            "email" to newEmail,
            "phoneNumber" to newPhoneNumber
        )

        userRepository.updateUserData(uid, userData)
            .addOnSuccessListener {
                Log.d(TAG, "User data updated successfully!")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error updating user data: ", e)
            }
    }

    private fun updateUserDataWithPassword(uid: String, newName: String, newEmail: String, newPhoneNumber: String, newPassword: String) {
        val userData = mapOf(
            "fullname" to newName,
            "email" to newEmail,
            "phoneNumber" to newPhoneNumber,
            "password" to newPassword
        )

        userRepository.updateUserData(uid, userData)
            .addOnSuccessListener {
                Log.d(TAG, "User data with password updated successfully!")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error updating user data with password: ", e)
            }
    }

    fun createImageUri(context: Context): Uri {
        val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile("profile", ".jpg", imagesDir)
        imageUri = FileProvider.getUriForFile(context, "com.example.app.fileprovider", imageFile)
        return imageUri!!
    }

}
