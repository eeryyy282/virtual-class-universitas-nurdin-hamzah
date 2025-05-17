package com.mjs.virtualclassuniversitasnurdinhamzah.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mjs.virtual_class_universitas_nurdin_hamzah.R

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val delaySplashScreen: Long = 2500
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val uri = "virtualclassuniversitasnurdinhamzah://onboarding".toUri()
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                finish()
            },
            delaySplashScreen
        )
    }
}
