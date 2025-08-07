package com.mjs.dosen.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Dosen
import com.mjs.core.domain.model.Tugas
import com.mjs.core.domain.repository.IVirtualClassRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(
    private val virtualClassRepository: IVirtualClassRepository,
    private val appPreference: AppPreference,
) : ViewModel() {
    private val _dosenData = MutableStateFlow<Resource<Dosen>?>(null)
    val dosenData: StateFlow<Resource<Dosen>?> = _dosenData

    private val _tugasListDosenState = MutableStateFlow<Resource<List<Tugas>>?>(null)
    val tugasListDosenState: StateFlow<Resource<List<Tugas>>?> = _tugasListDosenState

    private val _kelasDosenMapState = MutableStateFlow<Map<Int, String>>(emptyMap())
    val kelasDosenMapState: StateFlow<Map<Int, String>> = _kelasDosenMapState

    init {
        fetchDosenData()
        fetchTugasUntukDosen()
    }

    private fun fetchDosenData() {
        viewModelScope.launch {
            val nidn = appPreference.getLoggedInUserId().firstOrNull()
            if (nidn != null &&
                appPreference
                    .getLoggedInUserType()
                    .firstOrNull() == AppPreference.USER_TYPE_DOSEN
            ) {
                virtualClassRepository.getDosenByNidn(nidn).collect {
                    _dosenData.value = it
                }
            }
        }
    }

    private fun fetchTugasUntukDosen() {
        viewModelScope.launch {
            _tugasListDosenState.value = Resource.Loading()
            val nidn = appPreference.getLoggedInUserId().firstOrNull()

            if (nidn == null) {
                _tugasListDosenState.value = Resource.Error("NIDN dosen tidak ditemukan.")
                return@launch
            }

            val allKelasResource =
                virtualClassRepository
                    .getAllKelas()
                    .firstOrNull { it !is Resource.Loading }

            when (allKelasResource) {
                is Resource.Success -> {
                    val semuaKelas = allKelasResource.data ?: emptyList()
                    val kelasYangDiajar = semuaKelas.filter { it.nidn == nidn }

                    if (kelasYangDiajar.isEmpty()) {
                        _tugasListDosenState.value = Resource.Success(emptyList())
                        _kelasDosenMapState.value = emptyMap()
                        return@launch
                    }

                    _kelasDosenMapState.value =
                        kelasYangDiajar.associateBy({ it.kelasId }, { it.namaKelas })

                    val semuaTugasDariKelasDosen = mutableListOf<Tugas>()
                    var hasError = false
                    var errorMessage: String? = null

                    try {
                        val deferredTugasResources =
                            kelasYangDiajar.map { kelas ->
                                async {
                                    virtualClassRepository
                                        .getAssignmentsByClass(kelas.kelasId)
                                        .firstOrNull { it !is Resource.Loading }
                                }
                            }

                        val tugasResources = deferredTugasResources.awaitAll()

                        tugasResources.forEach { resource ->
                            when (resource) {
                                is Resource.Success -> {
                                    resource.data?.let { tugasList ->
                                        semuaTugasDariKelasDosen.addAll(tugasList)
                                    }
                                }

                                is Resource.Error -> {
                                    hasError = true
                                    if (errorMessage == null) {
                                        errorMessage = resource.message
                                    }
                                }

                                else -> {
                                    hasError = true
                                    if (errorMessage == null) {
                                        errorMessage =
                                            "Gagal memuat beberapa tugas (data tidak lengkap)."
                                    }
                                }
                            }
                        }

                        if (semuaTugasDariKelasDosen.isNotEmpty()) {
                            _tugasListDosenState.value =
                                Resource.Success(semuaTugasDariKelasDosen.distinctBy { it.assignmentId })
                        } else if (hasError) {
                            _tugasListDosenState.value =
                                Resource.Error(
                                    errorMessage ?: "Gagal memuat tugas untuk kelas dosen.",
                                )
                        } else {
                            _tugasListDosenState.value = Resource.Success(emptyList())
                        }
                    } catch (e: Exception) {
                        _tugasListDosenState.value =
                            Resource.Error("Terjadi kesalahan saat mengambil tugas: ${e.message}")
                    }
                }

                is Resource.Error -> {
                    _tugasListDosenState.value =
                        Resource.Error(
                            allKelasResource.message ?: "Gagal memuat daftar semua kelas.",
                        )
                }

                else -> {
                    _tugasListDosenState.value =
                        Resource.Error("Tidak dapat memuat informasi kelas untuk mengambil tugas.")
                }
            }
        }
    }

    fun getNamaKelasById(kelasId: Int): String = _kelasDosenMapState.value[kelasId] ?: "Kelas Tidak Dikenal"
}
