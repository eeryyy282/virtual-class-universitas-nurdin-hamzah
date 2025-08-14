package com.mjs.detailclass.registered

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mjs.detailclass.R
import com.mjs.detailclass.databinding.ActivityDetailClassRegisteredBinding
import com.mjs.detailclass.di.detailClassModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class DetailClassRegisteredActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailClassRegisteredBinding
    private val detailClassRegisteredViewModel: DetailClassRegisteredViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loadKoinModules(detailClassModule)
        binding = ActivityDetailClassRegisteredBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkDarkMode()
    }

    private fun checkDarkMode() {
        detailClassRegisteredViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
