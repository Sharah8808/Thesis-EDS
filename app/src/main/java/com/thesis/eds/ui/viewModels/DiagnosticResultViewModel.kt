package com.thesis.eds.ui.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.thesis.eds.data.model.HistoryDb
import com.thesis.eds.data.repository.HistoryRepository
import java.text.SimpleDateFormat
import java.util.*

class DiagnosticResultViewModel: ViewModel() {
    private val historyRepository = HistoryRepository()
    private val _history = MutableLiveData<HistoryDb?>()
    val history: MutableLiveData<HistoryDb?>
        get() = _history

    fun createNewHistory(predictRes : String, actualRes: String, imgUri : Uri){
        uploadImageToFirebaseStorage(imgUri, actualRes) { imageUrl ->
            val currentTimeStamp = getCurrentTimestamp()
            val currentUserId = historyRepository.getCurrentUser().uid
            val history = HistoryDb(
                "Diagnose result ${getTimestampForTitle()}",
                currentTimeStamp,
                currentUserId,
                predictRes,
                actualRes,
                imageUrl
            )

            history.let {
                historyRepository.addHistory(it)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Document successfully written
                            Log.d("EDSThesis_DResultVM", "New history document added.")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.d("EDSThesis_DResultVM", "Error adding new history document : ", e)
                    }
            }
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, actualName: String, callback: (String) -> Unit){
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val fileNameDate = getTimestampForTitle()
        val storageRef = FirebaseStorage.getInstance().reference.child("result images/$uid/$actualName - $fileNameDate.jpg")
        storageRef.putFile(imageUri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val imageUrl = task.result.toString()
                callback(imageUrl)
            } else {
                Log.d("EDSThesis_DResultVM", "Error uploading picture to Firebase Storage.")
            }
        }
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