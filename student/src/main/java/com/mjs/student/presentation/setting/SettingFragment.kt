package com.mjs.student.presentation.setting

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.mjs.core.data.Resource
import com.mjs.student.R
import com.mjs.student.databinding.FragmentSettingBinding
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
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        updateThemeDescription()
        setupAction()
        setupProfileUser()
    }

    private fun setupProfileUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingViewModel.studentProfileState.collectLatest {
                    when (it) {
                        is Resource.Error -> {
                            Toast
                                .makeText(
                                    context,
                                    it.message ?: "Terjadi kesalahan",
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }

                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            val student = it.data
                            if (student != null) {
                                binding.textViewUserNameValue.text = student.name
                                binding.textViewUserIdValue.text = student.studentId.toString()
                                Glide
                                    .with(requireContext())
                                    .load(student.profilePicture)
                                    .placeholder(R.drawable.profile_photo)
                                    .error(R.drawable.profile_photo)
                                    .into(binding.imageViewUserProfile)
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        getString(R.string.failed_to_load_student_data),
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
    }

    private fun setupAction() {
        binding.cardViewLanguageSettingsItem.setOnClickListener {
            Toast
                .makeText(
                    context,
                    getString(R.string.change_language_maintenance_message),
                    Toast.LENGTH_SHORT,
                ).show()
        }

        binding.cardViewThemeSettingsItem.setOnClickListener {
            val themes = arrayOf(getString(R.string.light_theme), getString(R.string.dark_theme))
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, themes)

            AlertDialog
                .Builder(requireContext())
                .setTitle(getString(R.string.choose_theme))
                .setAdapter(adapter) { _, which ->
                    when (which) {
                        0 -> settingViewModel.saveThemeSetting(false)
                        1 -> settingViewModel.saveThemeSetting(true)
                    }
                    Toast
                        .makeText(
                            context,
                            getString(R.string.theme_changed_to) + " " + themes[which],
                            Toast.LENGTH_SHORT,
                        ).show()
                }.show()
        }

        binding.cardViewProfileSettingsItem.setOnClickListener {
            val uri = "profile_settings://profile_settings_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        binding.buttonLogout.setOnClickListener {
            val dialog =
                AlertDialog
                    .Builder(requireContext())
                    .setTitle(getString(R.string.confirm_logout_title))
                    .setMessage(getString(R.string.confirm_logout_message))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        settingViewModel.logoutUser()
                        val uri = "virtualclassuniversitasnurdinhamzah://onboarding".toUri()
                        val logoutIntent = Intent(Intent.ACTION_VIEW, uri)
                        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(logoutIntent)
                        requireActivity().finish()
                    }.setNegativeButton(getString(R.string.no)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.outline_color_theme),
            )
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.outline_color_theme),
            )
        }
    }

    private fun updateThemeDescription() {
        settingViewModel.getThemeSetting.observe(viewLifecycleOwner) { isDarkMode ->
            val themeDescription =
                if (isDarkMode) getString(R.string.dark_theme) else getString(R.string.light_theme)
            binding.textViewThemeSettingsDescription.text =
                getString(R.string.current_theme, themeDescription)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
