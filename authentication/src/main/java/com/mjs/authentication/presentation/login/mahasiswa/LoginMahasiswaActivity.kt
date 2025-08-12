package com.mjs.authentication.presentation.login.mahasiswa

import android.app.ActivityOptions
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
import com.mjs.authentication.databinding.ActivityLoginMahasiswaBinding
import com.mjs.authentication.di.loginMahasiswaModule
import com.mjs.authentication.presentation.register.RegisterActivity
import com.mjs.authentication.presentation.utils.LoginResult
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
        observeLoginResult()
    }

    private fun setupAction() {
        binding.btnChangeTheme.setOnClickListener {
            loginMahasiswaViewModel.saveThemeSetting(
                loginMahasiswaViewModel.getThemeSetting.value == false,
            )
        }

        binding.btnRegisterLoginMahasiswa.setOnClickListener {
            val intent = Intent(this@LoginMahasiswaActivity, RegisterActivity::class.java)
            val options =
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out,
                )
            startActivity(intent, options.toBundle())
            finish()
        }

        binding.btnLoginMahasiswa.setOnClickListener {
            val nim =
                binding.etNim.text
                    .toString()
                    .trim()
            val password =
                binding.etPassword.text
                    .toString()
                    .trim()
            loginMahasiswaViewModel.login(nim, password)
        }
    }

    private fun observeLoginResult() {
        loginMahasiswaViewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val uri = "mahasiswa://mainactivity".toUri()
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
        loginMahasiswaViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
