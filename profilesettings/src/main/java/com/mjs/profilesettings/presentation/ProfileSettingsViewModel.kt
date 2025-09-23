package com.mjs.profilesettings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Lecturer
import com.mjs.core.domain.model.Student
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileSettingsViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _studentProfile = MutableLiveData<Resource<Student>>()
    val studentProfile: LiveData<Resource<Student>> = _studentProfile

    private val _lecturerProfile = MutableLiveData<Resource<Lecturer>>()
    val lecturerProfile: LiveData<Resource<Lecturer>> = _lecturerProfile

    private val _updateProfileResult = MutableLiveData<Resource<String>>()
    val updateProfileResult: LiveData<Resource<String>> = _updateProfileResult

    fun loadProfile() {
        viewModelScope.launch {
            val userId = virtualClassUseCase.getLoggedInUserId().first()
            val userType = virtualClassUseCase.getLoggedInUserType().first()

            if (userId != null && userType != null) {
                if (userType == VirtualClassUseCase.USER_TYPE_STUDENT) {
                    virtualClassUseCase.getStudentByStudentId(userId).collect {
                        _studentProfile.value = it
                    }
                } else if (userType == VirtualClassUseCase.USER_TYPE_LECTURER) {
                    virtualClassUseCase.getLecturerByLecturerId(userId).collect {
                        _lecturerProfile.value = it
                    }
                }
            }
        }
    }

    fun updateProfile(
        nama: String,
        email: String,
    ) {
        viewModelScope.launch {
            val userId = virtualClassUseCase.getLoggedInUserId().first()
            val userType = virtualClassUseCase.getLoggedInUserType().first()

            if (userId != null && userType != null) {
                _updateProfileResult.value = Resource.Loading()
                if (userType == VirtualClassUseCase.USER_TYPE_STUDENT) {
                    val currentStudent = studentProfile.value?.data
                    if (currentStudent != null) {
                        val updatedStudent =
                            currentStudent.copy(name = nama, email = email)
                        virtualClassUseCase.updateStudentProfile(updatedStudent).collect {
                            _updateProfileResult.value = it
                        }
                    } else {
                        _updateProfileResult.value =
                            Resource.Error("Data mahasiswa tidak ditemukan")
                    }
                } else if (userType == VirtualClassUseCase.USER_TYPE_LECTURER) {
                    val currentLecturer = lecturerProfile.value?.data
                    if (currentLecturer != null) {
                        val updatedLecturer =
                            currentLecturer.copy(name = nama, email = email)
                        virtualClassUseCase.updateLecturerProfile(updatedLecturer).collect {
                            _updateProfileResult.value = it
                        }
                    } else {
                        _updateProfileResult.value = Resource.Error("Data dosen tidak ditemukan")
                    }
                }
            } else {
                _updateProfileResult.value = Resource.Error("User tidak login")
            }
        }
    }
}
