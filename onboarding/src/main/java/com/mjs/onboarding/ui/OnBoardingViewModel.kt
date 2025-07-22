package com.mjs.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.domain.usecase.pref.ThemeUseCase
import kotlinx.coroutines.launch

class OnBoardingViewModel(
    private val themeUseCase: ThemeUseCase,
) : ViewModel() {
    val getThemeSetting = themeUseCase.getThemeSetting().asLiveData()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            themeUseCase.saveThemeSetting(isDarkModeActive)
        }
    }
}
