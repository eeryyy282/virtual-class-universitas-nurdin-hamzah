package com.mjs.dosen.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Dosen
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()
    private val _dosenData = MutableStateFlow<Resource<Dosen>?>(null)
    val dosenData: StateFlow<Resource<Dosen>?> = _dosenData

    init {
        fetchDosenData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            virtualClassUseCase.saveThemeSetting(isDarkModeActive)
        }
    }

    private fun fetchDosenData() {
        viewModelScope.launch {
            val nidn = virtualClassUseCase.getLoggedInUserId().firstOrNull()
            if (nidn != null &&
                virtualClassUseCase
                    .getLoggedInUserType()
                    .firstOrNull() == VirtualClassUseCase.USER_TYPE_DOSEN
            ) {
                virtualClassUseCase.getDosenByNidn(nidn).collect {
                    _dosenData.value = it
                }
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            virtualClassUseCase.clearLoginSession()
        }
    }
}
