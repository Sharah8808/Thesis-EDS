package com.thesis.eds.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.thesis.eds.data.model.HistoryDb

class HistoryRepository {

    private val firestore = Firebase.firestore
    private val historiesCollection = firestore.collection("histories")

    fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    fun updateHistoryData(historyId: String, historyData: Map<String, String?>): Task<Void> {
        return historiesCollection.document(historyId).update(historyData)
    }

    fun getHistoriesCollection(): Query {
        val currentUser = getCurrentUser()
        return historiesCollection.whereEqualTo("userId", currentUser.uid)
    }

    fun deleteHistoryData(documentId: String): Task<Void> {
        return historiesCollection.document(documentId).delete()
    }

    fun addHistory(history: HistoryDb) : Task<Void> {
        return historiesCollection.add(history)
            .continueWithTask { documentReference ->
                documentReference.result?.set(history)
            }
    }
}