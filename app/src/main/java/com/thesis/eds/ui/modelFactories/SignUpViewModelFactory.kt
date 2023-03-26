package com.thesis.eds.ui.modelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thesis.eds.ui.viewModels.SignUpViewModel

class SignUpViewModelFactory(private val auth: FirebaseAuth, private val db: FirebaseFirestore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(auth, db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
