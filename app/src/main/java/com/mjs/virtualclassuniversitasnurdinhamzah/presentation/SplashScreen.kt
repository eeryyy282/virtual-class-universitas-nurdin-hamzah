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
import com.mjs.virtual_class_universitas_nurdin_hamzah.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        playAnimation()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            binding.ivLogoSplashScreen.scaleX = 0.8f
            binding.ivLogoSplashScreen.scaleY = 0.8f

            binding.cvCopyrightSplashScreen.translationY = 50f
        }
    }

    private fun playAnimation() {
        binding.ivLogoSplashScreen.animate()
            .setDuration(1500)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .start()

        binding.cvCopyrightSplashScreen.animate()
            .setDuration(1500)
            .translationY(0f)
            .alpha(1f)
            .setStartDelay(500)
            .start()
    }
}
