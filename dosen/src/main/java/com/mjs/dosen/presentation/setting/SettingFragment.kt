package com.mjs.dosen.presentation.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.mjs.dosen.R
import com.mjs.dosen.databinding.FragmentSettingBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    val binding get() = _binding!!

    private val settingViewModel: SettingViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        updateThemeDescription()
        setupAction()

        return binding.root
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
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
