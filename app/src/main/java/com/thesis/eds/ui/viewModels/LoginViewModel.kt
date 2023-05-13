package com.thesis.eds.ui.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.CountDownLatch

class LoginViewModel(private val auth: FirebaseAuth) : ViewModel() {
    private val loginSuccess = MutableLiveData<Boolean>()
    fun login(email: String, password: String): LiveData<Boolean> {
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