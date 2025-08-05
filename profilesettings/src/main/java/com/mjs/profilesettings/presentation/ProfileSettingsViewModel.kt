package com.mjs.profilesettings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mjs.core.domain.usecase.pref.ThemeUseCase

class ProfileSettingsViewModel(
    themeUseCase: ThemeUseCase,
) : ViewModel() {
    val getThemeSetting = themeUseCase.getThemeSetting().asLiveData()
}
