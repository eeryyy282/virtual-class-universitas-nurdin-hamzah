package com.mjs.profilesettings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Dosen
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileSettingsViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _mahasiswaProfile = MutableLiveData<Resource<Mahasiswa>>()
    val mahasiswaProfile: LiveData<Resource<Mahasiswa>> = _mahasiswaProfile

    private val _dosenProfile = MutableLiveData<Resource<Dosen>>()
    val dosenProfile: LiveData<Resource<Dosen>> = _dosenProfile

    private val _updateProfileResult = MutableLiveData<Resource<String>>()
    val updateProfileResult: LiveData<Resource<String>> = _updateProfileResult

    fun loadProfile() {
        viewModelScope.launch {
            val userId = virtualClassUseCase.getLoggedInUserId().first()
            val userType = virtualClassUseCase.getLoggedInUserType().first()

            if (userId != null && userType != null) {
                if (userType == VirtualClassUseCase.USER_TYPE_MAHASISWA) {
                    virtualClassUseCase.getMahasiswaByNim(userId).collect {
                        _mahasiswaProfile.value = it
                    }
                } else if (userType == VirtualClassUseCase.USER_TYPE_DOSEN) {
                    virtualClassUseCase.getDosenByNidn(userId).collect {
                        _dosenProfile.value = it
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
                if (userType == VirtualClassUseCase.USER_TYPE_MAHASISWA) {
                    val currentMahasiswa = mahasiswaProfile.value?.data
                    if (currentMahasiswa != null) {
                        val updatedMahasiswa = currentMahasiswa.copy(nama = nama, email = email)
                        virtualClassUseCase.updateMahasiswaProfile(updatedMahasiswa).collect {
                            _updateProfileResult.value = it
                        }
                    } else {
                        _updateProfileResult.value =
                            Resource.Error("Data mahasiswa tidak ditemukan")
                    }
                } else if (userType == VirtualClassUseCase.USER_TYPE_DOSEN) {
                    val currentDosen = dosenProfile.value?.data
                    if (currentDosen != null) {
                        val updatedDosen = currentDosen.copy(nama = nama, email = email)
                        virtualClassUseCase.updateDosenProfile(updatedDosen).collect {
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
