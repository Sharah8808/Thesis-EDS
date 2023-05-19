package com.thesis.eds.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.thesis.eds.data.model.User

class UserRepository {

    private val firestore = Firebase.firestore
    private val usersCollection = firestore.collection("users")

    fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    fun getUserData(userId: String): Task<DocumentSnapshot> {
        return usersCollection.document(userId).get()
    }

    fun updateUserData(userId: String, userData: Map<String, String?>): Task<Void> {
        return usersCollection.document(userId).update(userData)
    }

    fun checkPassword(uid: String, password: String, callback: (Boolean) -> Unit) {
        val docRef = usersCollection.document(uid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                val isPasswordCorrect = user?.password == password
                Log.e(TAG, "Old Password = ${user?.password} ----------- New password = $password")
                callback(isPasswordCorrect)
            } else {
                callback(false)
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error checking password", e)
            callback(false)
        }
    }
}
