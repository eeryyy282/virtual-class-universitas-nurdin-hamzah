package com.mjs.authentication.presentation.login.dosen

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mjs.authentication.R
import com.mjs.authentication.databinding.ActivityLoginDosenBinding
import com.mjs.authentication.di.loginDeosenModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class LoginDosenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginDosenBinding
    private val loginDosenViewModel: LoginDosenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(loginDeosenModule)

        binding = ActivityLoginDosenBinding.inflate(layoutInflater)
        setContentView(
            binding.root,
        )
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkDarkMode()
        setupAction()
    }

    private fun setupAction() {
        binding.btnChangeTheme.setOnClickListener {
            loginDosenViewModel.saveThemeSetting(
                loginDosenViewModel.getThemeSetting.value == false,
            )
        }
    }

    private fun checkDarkMode() {
        loginDosenViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
