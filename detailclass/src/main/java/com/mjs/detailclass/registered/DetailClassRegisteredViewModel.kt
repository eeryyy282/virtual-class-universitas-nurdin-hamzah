package com.mjs.detailclass.registered

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Dosen
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class DetailClassRegisteredViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _kelasDetails = MutableLiveData<Resource<Kelas>>()
    val kelasDetails: LiveData<Resource<Kelas>> = _kelasDetails

    private val _dosenDetail = MutableLiveData<Resource<Dosen>>()
    val dosenDetail: LiveData<Resource<Dosen>> = _dosenDetail

    private val _leaveClassStatus = MutableLiveData<Resource<String>>()
    val leaveClassStatus: LiveData<Resource<String>> = _leaveClassStatus

    private val _loggedInUserId = MutableLiveData<Int?>()
    val loggedInUserId: LiveData<Int?> = _loggedInUserId

    private val _loggedInUserType = MutableLiveData<String?>()
    val loggedInUserType: LiveData<String?> = _loggedInUserType

    init {
        fetchLoggedInUserDetails()
    }

    private fun fetchLoggedInUserDetails() {
        viewModelScope.launch {
            virtualClassUseCase.getLoggedInUserId().collect { userId ->
                _loggedInUserId.value = userId
            }
        }
        viewModelScope.launch {
            virtualClassUseCase.getLoggedInUserType().collect { userType ->
                _loggedInUserType.value = userType
            }
        }
    }

    fun fetchClassDetails(kelasId: String) {
        viewModelScope.launch {
            _kelasDetails.value = Resource.Loading()
            virtualClassUseCase.getAllKelas().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val kelas = resource.data?.find { it.kelasId == kelasId }
                        if (kelas != null) {
                            _kelasDetails.value = Resource.Success(kelas)
                            fetchDosenDetails(kelas.nidn)
                        } else {
                            _kelasDetails.value = Resource.Error("Kelas tidak ditemukan")
                        }
                    }

                    is Resource.Error -> {
                        _kelasDetails.value =
                            Resource.Error(resource.message ?: "Gagal memuat detail kelas")
                    }

                    is Resource.Loading -> {
                    }
                }
            }
        }
    }

    private fun fetchDosenDetails(nidn: Int) {
        viewModelScope.launch {
            _dosenDetail.value = Resource.Loading()
            virtualClassUseCase.getDosenByNidn(nidn).collect { resource ->
                _dosenDetail.value = resource
            }
        }
    }

    fun leaveClass(
        nim: Int,
        kelasId: String,
    ) {
        viewModelScope.launch {
            _leaveClassStatus.value = Resource.Loading()
            virtualClassUseCase.leaveClass(nim, kelasId).collect {
                _leaveClassStatus.value = it
            }
        }
    }
}
