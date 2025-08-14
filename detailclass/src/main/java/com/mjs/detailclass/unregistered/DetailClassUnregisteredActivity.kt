package com.mjs.detailclass.unregistered

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mjs.detailclass.R
import com.mjs.detailclass.databinding.ActivityDetailClassUnregisteredBinding
import com.mjs.detailclass.di.detailClassModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class DetailClassUnregisteredActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailClassUnregisteredBinding
    private val activityDetailClassUnregisteredViewModel: DetailClassUnregisteredViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loadKoinModules(detailClassModule)
        binding = ActivityDetailClassUnregisteredBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkDarkMode()
    }

    private fun checkDarkMode() {
        activityDetailClassUnregisteredViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
