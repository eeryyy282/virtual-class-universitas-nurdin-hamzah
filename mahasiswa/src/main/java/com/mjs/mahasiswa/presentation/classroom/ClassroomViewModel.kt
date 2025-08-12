package com.mjs.mahasiswa.presentation.classroom

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
    private val _enrolledClasses = MutableLiveData<Resource<List<Kelas>>>()
    val enrolledClasses: LiveData<Resource<List<Kelas>>> = _enrolledClasses

    private val _dosenNamesMap = MutableLiveData<Map<String, String>>(emptyMap())
    val dosenNamesMap: LiveData<Map<String, String>> = _dosenNamesMap

    fun fetchEnrolledClasses() {
        viewModelScope.launch {
            _enrolledClasses.value = Resource.Loading()
            try {
                val nim = appPreference.getLoggedInUserId().first()
                if (nim != null) {
                    virtualClassUseCase.getAllSchedulesByNim(nim).collect { resource ->
                        if (resource is Resource.Success) {
                            resource.data?.let { classes ->
                                fetchDosenNamesForClasses(classes)
                            }
                        }
                        _enrolledClasses.value = resource
                    }
                } else {
                    _enrolledClasses.value =
                        Resource.Error("NIM pengguna tidak ditemukan. Silakan masuk kembali.")
                }
            } catch (e: Exception) {
                _enrolledClasses.value =
                    Resource.Error(e.message ?: "Gagal mengambil data kelas yang diikuti")
            }
        }
    }

    private fun fetchDosenNamesForClasses(classes: List<Kelas>) {
        viewModelScope.launch {
            classes.forEach { kelas ->
                val nidnString = kelas.nidn.toString()
                val currentNameInMap = _dosenNamesMap.value?.get(nidnString)

                if (currentNameInMap == null || currentNameInMap == nidnString) {
                    try {
                        virtualClassUseCase.getDosenByNidn(kelas.nidn).collect { dosenResource ->
                            val currentMapSnapshot = _dosenNamesMap.value ?: emptyMap()
                            val tempMapForUpdate = currentMapSnapshot.toMutableMap()
                            var needsLiveUpdate = false

                            when (dosenResource) {
                                is Resource.Success -> {
                                    dosenResource.data?.let { dosen ->
                                        if (tempMapForUpdate[nidnString] != dosen.nama) {
                                            tempMapForUpdate[nidnString] = dosen.nama
                                            needsLiveUpdate = true
                                        }
                                    } ?: run {
                                        if (tempMapForUpdate[nidnString] != nidnString) {
                                            tempMapForUpdate[nidnString] = nidnString
                                            needsLiveUpdate = true
                                        }
                                    }
                                }

                                is Resource.Error -> {
                                    if (tempMapForUpdate[nidnString] != nidnString) {
                                        tempMapForUpdate[nidnString] = nidnString
                                        needsLiveUpdate = true
                                    }
                                }

                                is Resource.Loading -> {
                                }
                            }

                            if (needsLiveUpdate) {
                                _dosenNamesMap.value = tempMapForUpdate
                            }
                        }
                    } catch (_: Exception) {
                        val currentMapSnapshot = _dosenNamesMap.value ?: emptyMap()
                        if (currentMapSnapshot[nidnString] != nidnString) {
                            val tempMapForUpdateOnError = currentMapSnapshot.toMutableMap()
                            tempMapForUpdateOnError[nidnString] = nidnString
                            _dosenNamesMap.value = tempMapForUpdateOnError
                        }
                    }
                }
            }
        }
    }

    fun getResolvedDosenName(nidn: Int): String {
        val nidnString = nidn.toString()
        return _dosenNamesMap.value?.get(nidnString) ?: nidnString
    }
}
