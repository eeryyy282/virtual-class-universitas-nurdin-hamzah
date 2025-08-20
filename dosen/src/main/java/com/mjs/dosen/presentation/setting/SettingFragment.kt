package com.mjs.dosen.presentation.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mjs.core.data.Resource
import com.mjs.dosen.R
import com.mjs.dosen.databinding.FragmentSettingBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val settingViewModel: SettingViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        updateThemeDescription()
        setupAction()
        setupProfileDosen()

        return binding.root
    }

    private fun setupProfileDosen() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingViewModel.dosenData.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        Toast
                            .makeText(
                                context,
                                it.message ?: getString(R.string.error),
                                Toast.LENGTH_SHORT,
                            ).show()
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val dosen = it.data
                        if (dosen != null) {
                            binding.tvName.text = dosen.nama
                            binding.tvIdUser.text = dosen.nidn.toString()
                            Glide
                                .with(requireContext())
                                .load(dosen.fotoProfil)
                                .placeholder(R.drawable.profile_photo)
                                .error(R.drawable.profile_photo)
                                .into(binding.photoProfileSetting)
                        } else {
                            Toast
                                .makeText(
                                    context,
                                    getString(R.string.failed_to_load_lecture_data),
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }
                    }

                    null -> {
                    }
                }
            }
        }
    }

    private fun updateThemeDescription() {
        settingViewModel.getThemeSetting.observe(viewLifecycleOwner) { isDarkMode ->
            val themeDescription =
                if (isDarkMode) getString(R.string.dark_theme) else getString(R.string.light_theme)
            binding.tvDescriptionIconTheme.text = themeDescription
        }
    }

    private fun setupAction() {
        binding.cvSettingLanguage.setOnClickListener {
            Toast
                .makeText(
                    context,
                    getString(R.string.change_language_maintenance_massage),
                    Toast.LENGTH_SHORT,
                ).show()
        }

        binding.cvSettingTheme.setOnClickListener {
            val themes = arrayOf(getString(R.string.light_theme), getString(R.string.dark_theme))
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, themes)

            AlertDialog
                .Builder(requireContext())
                .setTitle(getString(R.string.choose_theme))
                .setAdapter(adapter) { _, which ->
                    when (which) {
                        0 -> {
                            settingViewModel.saveThemeSetting(false)
                        }

                        1 -> {
                            settingViewModel.saveThemeSetting(true)
                        }
                    }
                    Toast
                        .makeText(
                            context,
                            getString(R.string.theme_changed_to) + " " + themes[which],
                            Toast.LENGTH_SHORT,
                        ).show()
                }.show()
        }

        binding.cvSettingProfileUser.setOnClickListener {
            val uri = "profile_settings://profile_settings_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            val dialog =
                AlertDialog
                    .Builder(requireContext())
                    .setTitle(getString(R.string.confirm_logout_title))
                    .setMessage(getString(R.string.confirm_logout_message))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        settingViewModel.logoutUser()
                        val uri = "virtualclassuniversitasnurdinhamzah://onboarding".toUri()
                        val logoutIntent =
                            Intent(Intent.ACTION_VIEW, uri).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            }
                        startActivity(logoutIntent)
                        requireActivity().finish()
                    }.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }.show()

            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.outline_color_theme,
                ),
            )
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.outline_color_theme,
                ),
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
