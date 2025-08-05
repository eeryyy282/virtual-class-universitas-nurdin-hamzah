package com.mjs.dosen.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.domain.usecase.pref.ThemeUseCase
import kotlinx.coroutines.launch

class SettingViewModel(
    private val themeUseCase: ThemeUseCase
) : ViewModel() {
    val getThemeSetting = themeUseCase.getThemeSetting().asLiveData()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            themeUseCase.saveThemeSetting(isDarkModeActive)
        }
    }
}