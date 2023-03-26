package com.thesis.eds.ui.modelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.thesis.eds.ui.viewModels.LoginViewModel

class LoginViewModelFactory (private val auth: FirebaseAuth) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(auth) as T
    }
}