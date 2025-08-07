package com.mjs.mahasiswa.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.repository.IVirtualClassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(
    private val virtualClassRepository: IVirtualClassRepository,
    private val appPreference: AppPreference,
) : ViewModel() {
    private val _mahasiswaData = MutableStateFlow<Resource<Mahasiswa>?>(null)
    val mahasiswaData: StateFlow<Resource<Mahasiswa>?> = _mahasiswaData

    init {
        fetchMahasiswaData()
    }

    private fun fetchMahasiswaData() {
        viewModelScope.launch {
            val nim = appPreference.getLoggedInUserId().firstOrNull()
            if (nim != null) {
                virtualClassRepository.getMahasiswaByNim(nim).collect {
                    _mahasiswaData.value = it
                }
            }
        }
    }
}
