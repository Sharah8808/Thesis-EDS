package com.thesis.eds.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.ktx.Firebase
import com.thesis.eds.databinding.ActivitySignUpBinding
import com.google.firebase.firestore.ktx.firestore
import timber.log.Timber

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var database : DatabaseReference

    private var database = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_up)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        database = Firebase.firestore
        firebaseAuth = FirebaseAuth.getInstance()
//        database = Firebase.database.reference

        binding.imgBack.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.fabSignUp.setOnClickListener{
            val name = binding.etFullname.text.toString()
            val email = binding.etEmail.text.toString()
            val phoneNumber = binding.etPhoneNumber.toString()
            val password = binding.etPassword.text.toString()
            val confirmPass = binding.etPasswordConfirm.text.toString()



            if(name.isNotBlank() && email.isNotBlank() && phoneNumber.isNotBlank() && password.isNotBlank() && confirmPass.isNotBlank()){
                if(password == confirmPass){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ it ->
                        if(it.isSuccessful){
                            val auth = firebaseAuth.currentUser
                            val userid = auth?.uid.toString()
                            createNewAccount(userid, name, phoneNumber, email, password)
//                            user?.let {
//                                Toast.makeText(this, "kebaca kah new user?", Toast.LENGTH_SHORT).show()
//                                val uid = user.uid
//                                writeNewUser(uid, fullname, email, password)
//                            }



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

    private fun createNewAccount(userId: String, name: String,phone: String, email: String, password: String) {
        val user = hashMapOf<String, Any?>(
            "email" to email,
            "name" to name,
            "phone" to phone,
            "password" to password,
            "img" to null
        )

        database.collection("users").document(userId).set(user)
            .addOnSuccessListener { Timber.tag(TAG).d("DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Timber.tag(TAG).w(e, "Error writing document") }

//        database.child("users").child(userId).setValue(user)
    }

    // create user account
//    private fun createAccount(email: String, password: String, username: String, storeName: String) {
////        loadingDialog.show()
//
//        // create user authentication
//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//            .addOnSuccessListener {
//                val user = auth.currentUser
//                val data = hashMapOf<String, Any?>(
//                    "uid" to user?.uid.toString(),
//                    "email" to email,
//                    "username" to username,
//                    "storeName" to storeName,
//                    "storeImage" to null,
//                    "status" to "offline",
//                    "rating" to 0.0,
//                    "nohp" to null,
//                    "lastSignTime" to null,
//                    "lastLocation" to null,
//                    "district" to null,
//                    "vendorToken" to null,
//                    "hasiot" to false
//                )
//
//                // create user data in firestore
//                database.collection("users")
//                    .document(user?.uid.toString())
//                    .set(data)
//                    .addOnSuccessListener {
//                        user?.sendEmailVerification()
////                        loadingDialog.dismiss()
//                        Snackbar.make(binding.parent, "Daftar Berhasil, periksa email anda untuk verifikasi akun", Snackbar.LENGTH_LONG).show()
//                        login()
//                    }
//                    .addOnFailureListener { e ->
//                        loadingDialog.dismiss()
//                        Toast.makeText(this@RegisterActivity, "Daftar Gagal. ${e.message}", Toast.LENGTH_SHORT).show()
//                    }
//            }
//            .addOnFailureListener {
//                loadingDialog.dismiss()
//                Toast.makeText(this@RegisterActivity, "Daftar Gagal. ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
}