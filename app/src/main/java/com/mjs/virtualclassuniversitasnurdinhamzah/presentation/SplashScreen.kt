package com.mjs.virtualclassuniversitasnurdinhamzah.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.virtual_class_universitas_nurdin_hamzah.R
import com.mjs.virtual_class_universitas_nurdin_hamzah.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private val splashScreenViewModel: SplashScreenViewModel by viewModel()
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

        checkSession()
        playAnimation()
        checkDarkMode()
    }

    private fun checkSession() {
        lifecycleScope.launch {
            val isLoggedIn = splashScreenViewModel.getLoginStatus().first()
            val userType = splashScreenViewModel.getUserType().first()

            if (isLoggedIn) {
                when (userType) {
                    AppPreference.USER_TYPE_MAHASISWA -> {
                        val uri = "mahasiswa://mainactivity".toUri()
                        navigateTo(uri)
                    }

                    AppPreference.USER_TYPE_DOSEN -> {
                        val uri = "dosen://mainactivity".toUri()
                        navigateTo(uri)
                    }

                    else -> {
                        navigateToOnboarding()
                    }
                }
            } else {
                navigateToOnboarding()
            }
        }
    }

    private fun navigateTo(uri: android.net.Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToOnboarding() {
        val delaySplashScreen: Long = 2500
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val uri = "virtualclassuniversitasnurdinhamzah://onboarding".toUri()
                navigateTo(uri)
            },
            delaySplashScreen,
        )
    }

    private fun checkDarkMode() {
        splashScreenViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
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
        binding.ivLogoSplashScreen
            .animate()
            .setDuration(1500)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .start()

        binding.cvCopyrightSplashScreen
            .animate()
            .setDuration(1500)
            .translationY(0f)
            .alpha(1f)
            .setStartDelay(500)
            .start()
    }
}
