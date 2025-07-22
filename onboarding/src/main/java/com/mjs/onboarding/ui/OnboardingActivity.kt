package com.mjs.onboarding.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mjs.onboarding.R
import com.mjs.onboarding.databinding.ActivityOnboardingBinding
import com.mjs.onboarding.di.onboardingModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(onboardingModule)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        changeTheme()
        setupAction()
        checkDarkMode()
    }

    private fun checkDarkMode() {
        onBoardingViewModel.getThemeSetting.observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setupAction() {
        with(binding) {
            val options =
                ActivityOptions.makeCustomAnimation(
                    this@OnboardingActivity,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out,
                )

            btnLoginAccountOnboarding.setOnClickListener {
                val uri = "authentication://login".toUri()
                val intentLogin = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intentLogin, options.toBundle())
            }
            btnLoginAsDosenOnboarding.setOnClickListener {
                val uri = "authentication://login_as_dosen".toUri()
                val intentLoginAsDosen = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intentLoginAsDosen, options.toBundle())
            }
            btnRegisterOnboarding.setOnClickListener {
                val uri = "authentication://register".toUri()
                val intentRegister = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intentRegister, options.toBundle())
            }
        }
    }

    private fun changeTheme() {
        binding.btnChangeTheme.setOnClickListener {
            onBoardingViewModel.saveThemeSetting(
                onBoardingViewModel.getThemeSetting.value == false,
            )
        }
    }
}
