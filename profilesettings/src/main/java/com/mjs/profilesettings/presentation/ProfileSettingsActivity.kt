package com.mjs.profilesettings.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mjs.core.data.Resource
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

        profileSettingsViewModel.loadProfile()
        observeViewModel()
        checkDarkMode()
        setupAction()
        setEditMode(false)
    }

    private fun observeViewModel() {
        profileSettingsViewModel.mahasiswaProfile.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val mahasiswa = it.data
                    binding.etProfileName.setText(mahasiswa?.nama)
                    binding.etProfileEmail.setText(mahasiswa?.email)
                    Glide
                        .with(this)
                        .load(mahasiswa?.fotoProfil)
                        .placeholder(R.drawable.profile_photo)
                        .error(R.drawable.profile_photo)
                        .into(binding.photoProfileSetting)
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        profileSettingsViewModel.dosenProfile.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val dosen = it.data
                    binding.etProfileName.setText(dosen?.nama)
                    binding.etProfileEmail.setText(dosen?.email)
                    Glide
                        .with(this)
                        .load(dosen?.fotoProfil)
                        .placeholder(R.drawable.profile_photo)
                        .error(R.drawable.profile_photo)
                        .into(binding.photoProfileSetting)
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        profileSettingsViewModel.updateProfileResult.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSaveProfile.isEnabled = false
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSaveProfile.isEnabled = true
                    Toast
                        .makeText(
                            this,
                            getString(R.string.text_changes_saved_successfully),
                            Toast.LENGTH_SHORT,
                        ).show()
                    setEditMode(false)
                    profileSettingsViewModel.loadProfile()
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSaveProfile.isEnabled = true
                    Toast
                        .makeText(
                            this,
                            "Gagal memperbarui profil: ${it.message}",
                            Toast.LENGTH_LONG,
                        ).show()
                    setEditMode(true)
                }
            }
        }
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
        if (!isEditing) {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setupAction() {
        binding.btnEditProfile.setOnClickListener {
            setEditMode(true)
        }

        binding.btnSaveProfile.setOnClickListener {
            val name =
                binding.etProfileName.text
                    .toString()
                    .trim()
            val email =
                binding.etProfileEmail.text
                    .toString()
                    .trim()

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Nama dan Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.text_confirm_changes_title))
                .setMessage(getString(R.string.text_confirm_changes_message))
                .setPositiveButton(getString(R.string.text_yes)) { _, _ ->
                    profileSettingsViewModel.updateProfile(name, email)
                }.setNegativeButton(getString(R.string.text_no)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
