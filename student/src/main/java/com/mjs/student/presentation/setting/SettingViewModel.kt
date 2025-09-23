package com.mjs.student.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Student
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _studentProfileState = MutableStateFlow<Resource<Student>?>(null)
    val studentProfileState: StateFlow<Resource<Student>?> = _studentProfileState

    init {
        loadStudentProfile()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            virtualClassUseCase.saveThemeSetting(isDarkModeActive)
        }
    }

    private fun loadStudentProfile() {
        viewModelScope.launch {
            _studentProfileState.value = Resource.Loading()
            val studentId = virtualClassUseCase.getLoggedInUserId().firstOrNull()
            if (studentId != null) {
                virtualClassUseCase.getStudentByStudentId(studentId).collect {
                    _studentProfileState.value = it
                }
            } else {
                _studentProfileState.value =
                    Resource.Error("Gagal mendapatkan ID pengguna. Silakan coba lagi.")
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            virtualClassUseCase.clearLoginSession()
        }
    }
}
