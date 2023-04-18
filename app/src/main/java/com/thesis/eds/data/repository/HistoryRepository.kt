package com.thesis.eds.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.thesis.eds.data.model.HistoryDb

class HistoryRepository {

    private val firestore = Firebase.firestore
    private val historiesCollection = firestore.collection("histories")

    fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    fun getHistoryData(historyId: String): Task<DocumentSnapshot> {
        return historiesCollection.document(historyId).get()
    }
    
    fun updateHistoryData(historyId: String, historyData: Map<String, String?>): Task<Void> {
        return historiesCollection.document(historyId).update(historyData)
    }

    fun createHistory(historyId: String, historyData: Map<String, Any>): Task<Void> {
        return historiesCollection.document(historyId).set(historyData)
    }

    fun addHistory(history: HistoryDb) : Task<Void> {
        return historiesCollection.add(history)
            .continueWithTask { documentReference ->
                documentReference.result?.set(history)
            }
    }
}