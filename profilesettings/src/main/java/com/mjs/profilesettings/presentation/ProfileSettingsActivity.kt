package com.mjs.profilesettings.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mjs.profilesettings.R
import com.mjs.profilesettings.databinding.ActivityProfileSettingsBinding
import com.mjs.profilesettings.di.profileSettingsModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class ProfileSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileSettingsBinding

    private val profileSettingsViewModel: ProfileSettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(profileSettingsModule)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkDarkMode()
        setupAction()
        setEditMode(false)
    }

    private fun checkDarkMode() {
        profileSettingsViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }

    private fun setEditMode(isEditing: Boolean) {
        binding.etProfileName.isEnabled = isEditing
        binding.etProfileEmail.isEnabled = isEditing
        binding.btnEditProfile.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.btnSaveProfile.visibility = if (isEditing) View.VISIBLE else View.GONE
    }

    private fun setupAction() {
        binding.btnEditProfile.setOnClickListener {
            setEditMode(true)
        }

        binding.btnSaveProfile.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.text_confirm_changes_title))
                .setMessage(getString(R.string.text_confirm_changes_message))
                .setPositiveButton(getString(R.string.text_yes)) { _, _ ->
                    binding.etProfileName.text.toString()
                    binding.etProfileEmail.text.toString()
                    Toast
                        .makeText(
                            this,
                            getString(R.string.text_changes_saved_successfully),
                            Toast.LENGTH_SHORT,
                        ).show()
                    setEditMode(false)
                }.setNegativeButton(getString(R.string.text_no)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
