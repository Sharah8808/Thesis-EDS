package com.thesis.trialnavdrawer.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.thesis.trialnavdrawer.R
import com.thesis.trialnavdrawer.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    private lateinit var firebaseAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_up)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.imgBack.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.fabSignUp.setOnClickListener{
            val fullname = binding.etFullname.text.toString()
            val email = binding.etEmail.text.toString()
            val phone_number = binding.etPhoneNumber.toString()
            val password = binding.etPassword.text.toString()
            val confirm_pass = binding.etPasswordConfirm.text.toString()

            if(fullname.isNotBlank() && email.isNotBlank() && phone_number.isNotBlank() && password.isNotBlank() && confirm_pass.isNotBlank()){
                if(password == confirm_pass){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if(it.isSuccessful){
                            Toast.makeText(this, "BALEK KE LOGIN ACTTTT", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java )
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}