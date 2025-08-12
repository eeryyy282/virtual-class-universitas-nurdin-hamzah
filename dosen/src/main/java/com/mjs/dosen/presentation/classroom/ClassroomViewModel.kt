package com.mjs.dosen.presentation.classroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ClassroomViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
    private val appPreference: AppPreference,
) : ViewModel() {
    private val _groupedClasses = MutableLiveData<Resource<List<Pair<String, List<Kelas>>>>>()
    val groupedClasses: LiveData<Resource<List<Pair<String, List<Kelas>>>>> = _groupedClasses

    private val _totalClasses = MutableLiveData<Int>()
    val totalClasses: LiveData<Int> = _totalClasses

    fun fetchDosenClasses() {
        viewModelScope.launch {
            _groupedClasses.value = Resource.Loading()
            _totalClasses.value = 0
            try {
                val nidn = appPreference.getLoggedInUserId().first()
                if (nidn != null) {
                    virtualClassUseCase.getAllSchedulesByNidn(nidn).collect { resource ->
                        if (resource is Resource.Success) {
                            val allClasses = resource.data ?: emptyList()
                            _totalClasses.value = allClasses.size
                            val grouped =
                                allClasses
                                    .groupBy { it.semester }
                                    .map { Pair(it.key, it.value) }
                            _groupedClasses.value = Resource.Success(grouped)
                        } else if (resource is Resource.Error) {
                            _groupedClasses.value =
                                Resource.Error(resource.message ?: "Gagal mengambil data kelas")
                        }
                    }
                } else {
                    _groupedClasses.value =
                        Resource.Error("NIDN pengguna tidak ditemukan. Silakan masuk kembali.")
                }
            } catch (e: Exception) {
                _groupedClasses.value = Resource.Error(e.message ?: "Gagal mengambil data kelas")
            }
        }
    }
}
