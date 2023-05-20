package com.thesis.eds.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(private val auth: FirebaseAuth) : ViewModel() {
    private val loginSuccess = MutableLiveData<Boolean>()

    private val _passwordResetResult = MutableLiveData<Boolean>()
    val passwordResetResult: LiveData<Boolean>
        get() = _passwordResetResult

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

    fun resetPassword(email : String){
        // Send a password reset email to the user
        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _passwordResetResult.value = task.isSuccessful
            }
    }
}