package com.thesis.eds.ui.viewModels

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Base64.encodeToString
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.eds.BuildConfig
import com.thesis.eds.data.model.User
import com.thesis.eds.data.repository.UserRepository
import java.io.File
import java.util.*

class EditProfileViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private var imageBase64: String? = null

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
            Log.d(TAG, "The old pass user typed?? == $oldPassword")
            oldPassword?.let {
                userRepository.checkPassword(currentUser.uid, oldPassword) { isPasswordCorrect ->
                    if (isPasswordCorrect) {
                        // Update password
                        newPassword?.let { it1 ->
                            updateUserDataWithPassword(currentUser.uid, newName, newEmail, newPhoneNumber, it1)
                        }
                    } else {
                        // Display error message to the user
                        Log.d(TAG, "Error update user data, password isn't correct")
                    }
                }
            }
        }
    }

    private fun updateUserDataWithoutPassword(uid: String, newName: String, newEmail: String, newPhoneNumber: String) {
        val userData = mapOf(
            "fullname" to newName,
            "email" to newEmail,
            "phoneNumber" to newPhoneNumber,

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
            "password" to newPassword,

        )

        userRepository.updateUserData(uid, userData)
            .addOnSuccessListener {
                Log.d(TAG, "User data with password updated successfully!")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error updating user data with password: ", e)
            }
    }

    fun updateUserProfileImage(uriLink : String){
        val imgData = mapOf(
            "img" to uriLink
        )
        userRepository.updateUserData(userRepository.getCurrentUser().uid, imgData)
            .addOnSuccessListener {
                Log.d(TAG, "User data with password updated successfully!")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error updating user data with password: ", e)
            }
    }



    fun createImageUri(context: Context): Uri {
        val imageName = "${System.currentTimeMillis()}.jpg"
        val imageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imageDir, imageName)
        imageFile.createNewFile()
        val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+ ".provider", imageFile)
        imageBase64 = encodeImageToBase64(imageFile)
        Log.d(TAG, "The imageBase64 ??? == $imageBase64 -------------------")
        return uri
    }

    private fun encodeImageToBase64(imageFile: File): String {
        val bytes = imageFile.readBytes()
        return encodeToString(bytes, Base64.DEFAULT)
    }

}
