package com.mjs.detailclass.unregistered

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.domain.model.Dosen
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailClassUnregisteredViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _kelasDetails = MutableLiveData<Resource<Kelas>>()
    val kelasDetails: LiveData<Resource<Kelas>> = _kelasDetails

    private val _dosenDetails = MutableLiveData<Resource<Dosen>>()
    val dosenDetails: LiveData<Resource<Dosen>> = _dosenDetails

    private val _mahasiswaList = MutableLiveData<Resource<List<Mahasiswa>>>()
    val mahasiswaList: LiveData<Resource<List<Mahasiswa>>> = _mahasiswaList

    private val _mahasiswaCount = MutableLiveData<Resource<Int>>()
    val mahasiswaCount: LiveData<Resource<Int>> = _mahasiswaCount

    private val _enrollmentRequestStatus = MutableLiveData<Resource<String>>()
    val enrollmentRequestStatus: LiveData<Resource<String>> = _enrollmentRequestStatus

    private val _currentEnrollmentState = MutableLiveData<EnrollmentEntity?>()
    val currentEnrollmentState: LiveData<EnrollmentEntity?> = _currentEnrollmentState

    fun fetchKelasDetailsAndEnrollmentStatus(kelasId: String) {
        viewModelScope.launch {
            val nim = virtualClassUseCase.getLoggedInUserId().first()
            if (nim != null) {
                virtualClassUseCase.getEnrolledClasses(nim).collect { resource ->
                    if (resource is Resource.Success) {
                        val currentEnrollment = resource.data?.find { it.kelasId == kelasId }
                        _currentEnrollmentState.value = currentEnrollment
                    }
                }
            }
        }

        viewModelScope.launch {
            virtualClassUseCase.getKelasById(kelasId).collect {
                _kelasDetails.value = it
                if (it is Resource.Success) {
                    it.data?.nidn?.let { nidn ->
                        fetchDosenDetails(nidn)
                    }
                    fetchMahasiswaByKelasId(kelasId)
                    fetchMahasiswaCountByKelasId(kelasId)
                }
            }
        }
    }

    private fun fetchDosenDetails(nidn: Int) {
        viewModelScope.launch {
            virtualClassUseCase.getDosenByNidn(nidn).collect {
                _dosenDetails.value = it
            }
        }
    }

    private fun fetchMahasiswaByKelasId(kelasId: String) {
        viewModelScope.launch {
            virtualClassUseCase
                .getMahasiswaByKelasId(kelasId)
                .collect {
                    _mahasiswaList.value = it
                }
        }
    }

    private fun fetchMahasiswaCountByKelasId(kelasId: String) {
        viewModelScope.launch {
            virtualClassUseCase
                .getMahasiswaCountByKelasId(kelasId)
                .collect {
                    _mahasiswaCount.value = it
                }
        }
    }

    fun enrollToClass(kelasId: String) {
        viewModelScope.launch {
            try {
                _enrollmentRequestStatus.value = Resource.Loading()
                val nim = virtualClassUseCase.getLoggedInUserId().first()
                if (nim == null) {
                    _enrollmentRequestStatus.value = Resource.Error("User tidak login")
                    return@launch
                }
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val currentDate = dateFormat.format(Date())
                val enrollment =
                    EnrollmentEntity(
                        nim = nim,
                        kelasId = kelasId,
                        tanggalDaftar = currentDate,
                        status = "pending",
                    )
                virtualClassUseCase.enrollToClass(enrollment).collect {
                    _enrollmentRequestStatus.value = it
                    if (it is Resource.Success) {
                        _currentEnrollmentState.value = enrollment
                    }
                }
            } catch (e: Exception) {
                _enrollmentRequestStatus.value =
                    Resource.Error(e.message ?: "Gagal mendaftar ke kelas")
            }
        }
    }
}
