package com.mjs.detailclass.unregistered

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Dosen
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

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

    fun fetchKelasDetails(kelasId: String) {
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
            virtualClassUseCase.getMahasiswaByKelasId(kelasId).collect {
                _mahasiswaList.value = it
            }
        }
    }

    private fun fetchMahasiswaCountByKelasId(kelasId: String) {
        viewModelScope.launch {
            virtualClassUseCase.getMahasiswaCountByKelasId(kelasId).collect {
                _mahasiswaCount.value = it
            }
        }
    }
}
