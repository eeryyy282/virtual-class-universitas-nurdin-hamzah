package com.mjs.lecturer.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Lecturer
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()
    private val _lecturerData = MutableStateFlow<Resource<Lecturer>?>(null)
    val lecturerData: StateFlow<Resource<Lecturer>?> = _lecturerData

    init {
        fetchLecturerData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            virtualClassUseCase.saveThemeSetting(isDarkModeActive)
        }
    }

    private fun fetchLecturerData() {
        viewModelScope.launch {
            val lecturerId = virtualClassUseCase.getLoggedInUserId().firstOrNull()
            if (lecturerId != null &&
                virtualClassUseCase
                    .getLoggedInUserType()
                    .firstOrNull() == VirtualClassUseCase.USER_TYPE_LECTURER
            ) {
                virtualClassUseCase.getLecturerByLecturerId(lecturerId).collect {
                    _lecturerData.value = it
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
