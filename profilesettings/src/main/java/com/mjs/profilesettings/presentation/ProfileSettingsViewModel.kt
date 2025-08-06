package com.mjs.profilesettings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase

class ProfileSettingsViewModel(
    virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()
}
