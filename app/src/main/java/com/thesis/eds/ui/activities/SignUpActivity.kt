package com.thesis.eds.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.ktx.Firebase
import com.thesis.eds.databinding.ActivitySignUpBinding
import com.google.firebase.firestore.ktx.firestore
import com.thesis.eds.ui.modelFactories.SignUpViewModelFactory
import com.thesis.eds.ui.viewModels.SignUpViewModel

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory(
            FirebaseAuth.getInstance(),
            Firebase.firestore
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.imgBack.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.fabSignUp.setOnClickListener{
            val name = binding.etFullname.text.toString()
            val email = binding.etEmail.text.toString()
            val phoneNumber = binding.etPhoneNumber.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPass = binding.etPasswordConfirm.text.toString()

            if(name.isNotBlank() && email.isNotBlank() && phoneNumber.isNotBlank() && password.isNotBlank() && confirmPass.isNotBlank()) {
                viewModel.signUp(name, email, phoneNumber, password, confirmPass)

                viewModel.signupSuccess.observe(this) { success ->
                    if (success) {
                        Log.d("EDSThesis_SignUpAct", "Sign in success. Back to login activity.")
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                viewModel.signupError.observe(this) { error ->
                    // SignUp is failed, show the error message here
                    Log.d("EDSThesis_SignUpAct", "Sign in failed.")
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
