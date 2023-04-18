package com.thesis.eds.ui.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.thesis.eds.data.model.History
import com.thesis.eds.utils.Dummy

class HistoryViewModel: ViewModel() {
    fun getHistoryList():List<History> = Dummy.getDummyHistory()

//    val db = Firebase.firestore
//    val userId = Firebase.auth.currentUser?.uid
//
//    if (userId != null) {
//        db.collection("histories")
//            .whereEqualTo("userId", userId)
//            .get()
//            .addOnSuccessListener { documents ->
//                // Here, 'documents' is a QuerySnapshot containing all the history records
//                // that belong to the current user (in this case, Gale)
//                val historyList = mutableListOf<History>()
//
//                for (document in documents) {
//                    // Here, we create a History object from each Firestore document
//                    val history = History(
//                        document.getString("name") ?: "",
//                        document.getDate("timestamp") ?: Date(),
//                        document.getString("field1") ?: "",
//                        document.getString("field2") ?: ""
//                    )
//
//                    historyList.add(history)
//                }
//
//                // TODO: Use the 'historyList' to display the history records in a RecyclerView
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "Error getting history records: ", exception)
//            }
//    }

}