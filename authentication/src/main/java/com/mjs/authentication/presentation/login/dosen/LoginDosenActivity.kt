package com.mjs.authentication.presentation.login.dosen

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
import com.mjs.authentication.databinding.ActivityLoginDosenBinding
import com.mjs.authentication.di.loginDosenModule
import com.mjs.authentication.presentation.utils.LoginResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class LoginDosenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginDosenBinding
    private val loginDosenViewModel: LoginDosenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(loginDosenModule)
        binding = ActivityLoginDosenBinding.inflate(layoutInflater)
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
            loginDosenViewModel.saveThemeSetting(
                loginDosenViewModel.getThemeSetting.value == false,
            )
        }

        binding.btnLoginDosen.setOnClickListener {
            val nidn =
                binding.etNip.text
                    .toString()
                    .trim()
            val password =
                binding.etPassword.text
                    .toString()
                    .trim()
            loginDosenViewModel.login(nidn, password)
        }
    }

    private fun observeLoginResult() {
        loginDosenViewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val uri = "dosen://mainactivity".toUri()
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
        loginDosenViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
