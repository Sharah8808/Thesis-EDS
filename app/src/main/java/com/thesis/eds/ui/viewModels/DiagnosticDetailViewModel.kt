package com.thesis.eds.ui.viewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.thesis.eds.data.model.History
import com.thesis.eds.data.model.HistoryDb
import com.thesis.eds.data.repository.HistoryRepository
import com.thesis.eds.utils.Dummy
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DiagnosticDetailViewModel : ViewModel() {

    private val historyRepository = HistoryRepository()
    private val _history = MutableLiveData<HistoryDb?>()
    val history: MutableLiveData<HistoryDb?>
        get() = _history

    fun loadHistoryData(timeStamp: String){
        historyRepository.getHistoriesCollection().whereEqualTo("timeStamp" , timeStamp)
            .addSnapshotListener { value, error ->
            if (error != null) {
                Log.e(TAG, "Error getting history data", error)
                return@addSnapshotListener
            }
            if (value != null && !value.isEmpty) {
                // Get the first document from the query result
                val documentSnapshot = value.documents[0]
                // Convert the document snapshot to a HistoryDb object
                val history = documentSnapshot.toObject(HistoryDb::class.java)
                // Update the _history MutableLiveData with the loaded history
                _history.postValue(history)
            }
        }
    }
    
    fun editHistoryName(editedName : String , timeStamp: String){
        val historyQuery = historyRepository.getHistoriesCollection()
            .whereEqualTo("timeStamp" , timeStamp)
        historyQuery.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentId = querySnapshot.documents[0].id
                    historyRepository.updateHistoryData(documentId, mapOf("name" to editedName))
                        .addOnSuccessListener {
                            Log.d(TAG, "History name updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error updating history name", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error querying histories collection", e)
            }
    }

//    fun downloadPicture(timeStamp: String) {
//        historyRepository.getHistoriesCollection().whereEqualTo("timeStamp", timeStamp)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                if (!querySnapshot.isEmpty) {
//                    val historyDb = querySnapshot.documents[0].toObject(HistoryDb::class.java)
//                    historyDb?.let { history ->
//                        val storageRef = Firebase.storage.reference.child(history.img)
//                        val localFile = File.createTempFile(history.name, "png")
//                        storageRef.getFile(localFile)
//                            .addOnSuccessListener {
//                                // Image downloaded successfully
//                                Log.d(TAG, "Image downloaded successfully")
//                                // TODO: Do something with the downloaded image
//                            }
//                            .addOnFailureListener { e ->
//                                // Error downloading image
//                                Log.e(TAG, "Error downloading image", e)
//                            }
//                    }
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e(TAG, "Error querying history document", e)
//            }
//    }

    fun downloadPicture(timeStamp: String, result : String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = historyRepository.getCurrentUser().uid
        val formattedTime = changeTimeFormat(timeStamp)
        historyRepository.getHistoriesCollection().whereEqualTo("timeStamp", timeStamp)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val historyDb = querySnapshot.documents[0].toObject(HistoryDb::class.java)
                    historyDb?.let { history ->
                        val storageRef = Firebase.storage.reference.child("result images/$userId/$result - $formattedTime.jpg")
                        Log.d(TAG, "File name wheeeere whaaattt ?? result images/$userId/$result - $formattedTime.jpg ==============================================")
                        val localFile = File.createTempFile(history.name, "png")
                        storageRef.getFile(localFile)
                            .addOnSuccessListener {
                                // Image downloaded successfully
                                Log.d(TAG, "Image downloaded successfully")
                                // Call the onSuccess lambda and pass the filePath
                                onSuccess(localFile.absolutePath)
                            }
                            .addOnFailureListener { e ->
                                // Call the onFailure lambda and pass the exception
                                onFailure(e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }


    fun eraseHistory(timeStamp: String) {
        val historyQuery = historyRepository.getHistoriesCollection()
            .whereEqualTo("timeStamp", timeStamp)

        historyQuery.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents[0].id
                historyRepository.deleteHistoryData(documentId)
                    .addOnSuccessListener {
                        Log.d(TAG, "History data deleted successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error deleting history data", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error querying histories collection", e)
        }
    }

    private fun changeTimeFormat(timeStamp: String) : String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm", Locale.getDefault())
        val date = inputFormat.parse(timeStamp)
        return date?.let { outputFormat.format(it) }

    }
}