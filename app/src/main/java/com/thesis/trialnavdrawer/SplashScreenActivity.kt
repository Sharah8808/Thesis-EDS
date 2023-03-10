package com.thesis.trialnavdrawer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.thesis.trialnavdrawer.R
import com.thesis.trialnavdrawer.ui.login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var handler : Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()
        handler = Handler(mainLooper)
        handler.postDelayed({
            val intent  = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}