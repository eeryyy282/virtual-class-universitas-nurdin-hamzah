package com.mjs.mahasiswa.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class SettingViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            virtualClassUseCase.saveThemeSetting(isDarkModeActive)
        }
    }
}
