package com.mjs.authentication.presentation.login.lecturer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mjs.authentication.R
import com.mjs.authentication.databinding.ActivityLoginLecturerBinding
import com.mjs.authentication.di.loginLecturerModule
import com.mjs.authentication.presentation.utils.LoginResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class LoginLecturerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginLecturerBinding
    private val loginLecturerViewModel: LoginLecturerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(loginLecturerModule)
        binding = ActivityLoginLecturerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkDarkMode()
        setupAction()
        observeLoginResult()
    }

    private fun setupAction() {
        binding.btnChangeTheme.setOnClickListener {
            loginLecturerViewModel.saveThemeSetting(
                loginLecturerViewModel.getThemeSetting.value == false,
            )
        }

        binding.btnLoginLecturer.setOnClickListener {
            val lecturerId =
                binding.etNip.text
                    .toString()
                    .trim()
            val password =
                binding.etPassword.text
                    .toString()
                    .trim()
            loginLecturerViewModel.login(lecturerId.toLong(), password)
        }
    }

    private fun observeLoginResult() {
        loginLecturerViewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                    val uri = "lecturer://mainactivity".toUri()
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                is LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkDarkMode() {
        loginLecturerViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
