package com.thesis.eds.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserRepository {

    private val firestore = Firebase.firestore
    private val usersCollection = firestore.collection("users")

    fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    fun getUserData(userId: String): Task<DocumentSnapshot> {
        return usersCollection.document(userId).get()
    }

    fun updateUserData(userId: String, userData: Map<String, Any>): Task<Void> {
        return usersCollection.document(userId).update(userData)
    }

    fun createUser(userId: String, userData: Map<String, Any>): Task<Void> {
        return usersCollection.document(userId).set(userData)
    }
}
