package com.mjs.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mjs.onboarding.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        changeTheme()
        setupAction()
    }

    private fun setupAction() {
        with(binding) {
            btnLoginAccountOnboarding.setOnClickListener {
                val uri = "authentication://login".toUri()
                val intentLogin = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intentLogin)
            }
            btnLoginAsDosenOnboarding.setOnClickListener {
                val uri = "authentication://login_as_dosen".toUri()
                val intentLoginAsDosen = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intentLoginAsDosen)
            }
            btnRegisterOnboarding.setOnClickListener {
                val uri = "authentication://register".toUri()
                val intentRegister = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intentRegister)
            }
        }
    }

    private fun changeTheme() {
        binding.btnChangeTheme.setOnClickListener {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
}
