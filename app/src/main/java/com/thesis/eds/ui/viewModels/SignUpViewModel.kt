package com.thesis.eds.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpViewModel(private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore) : ViewModel() {
    private val _signupSuccess = MutableLiveData<Boolean>()
    val signupSuccess: LiveData<Boolean>
        get() = _signupSuccess

    private val _signupError = MutableLiveData<String>()
    val signupError: LiveData<String>
        get() = _signupError

    fun signUp(name: String, email: String, phone: String, password: String, confirmPassword: String) {
        if (password == confirmPassword) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid ?: ""
                        val user = hashMapOf(
                            "fullname" to name,
                            "email" to email,
                            "phoneNumber" to phone,
                            "img" to null,
                            "password" to password
                        )
                        firestore.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                _signupSuccess.postValue(true)
                            }
                            .addOnFailureListener { e ->
                                _signupError.postValue(e.localizedMessage)
                            }
                    } else {
                        _signupError.postValue(task.exception?.localizedMessage)
                    }
                }
        } else {
            _signupError.postValue("Passwords do not match")
        }
    }
}
