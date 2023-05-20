package com.thesis.eds.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        binding.textForgotPassword.setOnClickListener{
            showForgotPasswordDialog()
        }

        binding.fabLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPaswword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                viewModel.login(email, password).observe(this) { loginSuccess ->
                    if (loginSuccess) {
                        Log.d("EDSThesis_LoginAct", "Login process is a success.")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.d("EDSThesis_LoginAct", "Login process is failed.")
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Log.d("EDSThesis_LoginAct", "Empty login edit text.")
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

    private fun showForgotPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ketik email untuk reset password")
        // Set up the input
        val input = EditText(this)
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Lanjut") { _, _ ->
            // Call the callback function with the text from the input field
            val email = input.text.toString()
            if(isValidEmail(email)){
                viewModel.resetPassword(email)
                viewModel.passwordResetResult.observe(this) { success ->
                    if (success) {
                        Log.d("EDSThesis_LoginAct", "Reset password process is success.")
                        Toast.makeText(
                            this,
                            "Password reset email sent successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d("EDSThesis_LoginAct", "Reset password process is fail.")
                        Toast.makeText(
                            this,
                            "Failed to send password reset email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.cancel()
        }

        // Show the dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun isValidEmail(email: String): Boolean {
        // TODO: Implement email validation logic if needed
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}




