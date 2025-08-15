package com.mjs.mahasiswa.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()
    private val _mahasiswaData = MutableStateFlow<Resource<Mahasiswa>?>(null)
    val mahasiswaData: StateFlow<Resource<Mahasiswa>?> = _mahasiswaData

    init {
        fetchMahasiswaData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            virtualClassUseCase.saveThemeSetting(isDarkModeActive)
        }
    }

    private fun fetchMahasiswaData() {
        viewModelScope.launch {
            val nim = virtualClassUseCase.getLoggedInUserId().firstOrNull()

            if (nim != null) {
                virtualClassUseCase.getMahasiswaByNim(nim).collect {
                    _mahasiswaData.value = it
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
