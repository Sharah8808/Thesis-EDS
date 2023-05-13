package com.thesis.eds.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.thesis.eds.databinding.ActivityLoginBinding
import com.thesis.eds.ui.modelFactories.LoginViewModelFactory
import com.thesis.eds.ui.viewModels.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this,
            LoginViewModelFactory(firebaseAuth))[LoginViewModel::class.java]

        binding.signUpBottom.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.fabLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPaswword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                viewModel.login(email, password).observe(this) { loginSuccess ->
                    if (loginSuccess) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}




