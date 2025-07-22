package com.mjs.authentication.presentation.login.mahasiswa

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mjs.authentication.R
import com.mjs.authentication.databinding.ActivityLoginMahasiswaBinding
import com.mjs.authentication.di.loginMahasiswaModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class LoginMahasiswaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginMahasiswaBinding
    private val loginMahasiswaViewModel: LoginMahasiswaViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(loginMahasiswaModule)
        binding = ActivityLoginMahasiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            loginMahasiswaViewModel.saveThemeSetting(
                loginMahasiswaViewModel.getThemeSetting.value == false,
            )
        }
    }

    private fun checkDarkMode() {
        loginMahasiswaViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
