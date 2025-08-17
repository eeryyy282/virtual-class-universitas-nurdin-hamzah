package com.mjs.detailclass.unregistered

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

class DetailClassUnregisteredViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _kelasDetails = MutableLiveData<Resource<Kelas>>()
    val kelasDetails: LiveData<Resource<Kelas>> = _kelasDetails

    private val _dosenDetails = MutableLiveData<Resource<Dosen>>()
    val dosenDetails: LiveData<Resource<Dosen>> = _dosenDetails

    fun fetchKelasDetails(kelasId: String) {
        viewModelScope.launch {
            virtualClassUseCase.getKelasById(kelasId).collect {
                _kelasDetails.value = it
                if (it is Resource.Success) {
                    it.data?.nidn?.let { nidn ->
                        fetchDosenDetails(nidn)
                    }
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
}
