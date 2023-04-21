package com.thesis.eds.ui.viewModels

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.thesis.eds.data.model.HistoryDb
import com.thesis.eds.data.model.User
import com.thesis.eds.data.repository.HistoryRepository
import java.text.SimpleDateFormat
import java.time.LocalDateTime.now
import java.util.*
import com.google.firebase.Timestamp


class DiagnosticResultViewModel: ViewModel() {

    private val historyRepository = HistoryRepository()
    private val _history = MutableLiveData<HistoryDb?>()
    val history: MutableLiveData<HistoryDb?>
        get() = _history

    var uri : Uri? = null

    fun createNewHistory(predictRes : String, actualRes: String, imgUri : Uri){
        uploadImageToFirebaseStorage(imgUri, actualRes)

        val currentTimeStamp = getCurrentTimestamp()
        val currentUserId = historyRepository.getCurrentUser().uid
        val uriString = imgUri.toString()
        val history = HistoryDb(
            "Diagnose result ${getTimestampForTitle()}",
            currentTimeStamp,
            currentUserId,
            predictRes,
            actualRes,
            uriString
        )

        historyRepository.addHistory(history)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Document successfully written
                    Log.d(TAG, "New history document added.")
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error adding new history document. : ", e)
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, actualName: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val fileNameDate = getTimestampForTitle()
        FirebaseStorage.getInstance()
            .reference
            .child("result images/$uid/$actualName - $fileNameDate.jpg")
            .putFile(imageUri)

    }

    private fun getCurrentTimestamp(): String {
        val timestamp = Timestamp.now().toDate()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(timestamp)
    }

    private fun getTimestampForTitle(): String {
        val timestamp = Timestamp.now().toDate()
        val dateFormat = SimpleDateFormat("h:mm", Locale.getDefault())
        return dateFormat.format(timestamp)
    }
}