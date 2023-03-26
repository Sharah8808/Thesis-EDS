package com.thesis.eds.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(private val auth: FirebaseAuth) : ViewModel() {

    fun login(email: String, password: String): LiveData<Boolean> {
        val loginSuccess = MutableLiveData<Boolean>()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginSuccess.postValue(true)
                } else {
                    loginSuccess.postValue(false)
                }
            }
        return loginSuccess
    }

}