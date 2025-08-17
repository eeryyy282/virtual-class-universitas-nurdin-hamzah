package com.mjs.enrollclass.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EnrollClassViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    @Suppress("ktlint:standard:backing-property-naming")
    private val _originalGroupedClasses =
        MutableLiveData<Resource<List<Pair<String, List<Kelas>>>>>()

    private val _groupedClasses = MutableLiveData<Resource<List<Pair<String, List<Kelas>>>>>()
    val groupedClasses: LiveData<Resource<List<Pair<String, List<Kelas>>>>> = _groupedClasses

    private val searchQuery = MutableLiveData<String>()

    private val _dosenMap = MutableLiveData<Map<Int, String>>()
    val dosenMap: LiveData<Map<Int, String>> = _dosenMap

    init {
        fetchUnenrolledClassesByStudentMajor()
        searchQuery.observeForever { query ->
            filterClasses(query)
        }
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    private fun filterClasses(query: String?) {
        val originalResource = _originalGroupedClasses.value
        if (originalResource is Resource.Success) {
            val originalData = originalResource.data
            if (query.isNullOrBlank()) {
                _groupedClasses.value = originalResource
            } else {
                val filteredList =
                    originalData?.mapNotNull { (semester, kelasList) ->
                        val filteredKelas =
                            kelasList.filter { kelas ->
                                kelas.namaKelas.contains(query, ignoreCase = true) ||
                                    kelas.kelasId.contains(query, ignoreCase = true) ||
                                    kelas.semester.contains(query, ignoreCase = true)
                            }
                        if (filteredKelas.isNotEmpty()) {
                            semester to filteredKelas
                        } else {
                            null
                        }
                    }
                if (filteredList.isNullOrEmpty()) {
                    _groupedClasses.value = Resource.Success(emptyList())
                } else {
                    _groupedClasses.value = Resource.Success(filteredList)
                }
            }
        } else {
            if (originalResource != null) {
                _groupedClasses.value = originalResource
            } else {
                _groupedClasses.value = Resource.Loading()
            }
        }
    }

    private fun fetchDosenName(nidn: Int) {
        viewModelScope.launch {
            virtualClassUseCase.getDosenByNidn(nidn).collect { resource ->
                if (resource is Resource.Success) {
                    resource.data?.let { dosen ->
                        val currentMap = _dosenMap.value ?: emptyMap()
                        _dosenMap.value = currentMap + (nidn to dosen.nama)
                    }
                }
            }
        }
    }

    private fun fetchUnenrolledClassesByStudentMajor() {
        viewModelScope.launch {
            _originalGroupedClasses.value = Resource.Loading()
            _groupedClasses.value = Resource.Loading()
            try {
                val nim = virtualClassUseCase.getLoggedInUserId().first()
                if (nim != null) {
                    val userType = virtualClassUseCase.getLoggedInUserType().first()
                    if (userType == VirtualClassUseCase.USER_TYPE_MAHASISWA) {
                        virtualClassUseCase.getMahasiswaByNim(nim).collect { mahasiswaResource ->
                            when (mahasiswaResource) {
                                is Resource.Success -> {
                                    val mahasiswa = mahasiswaResource.data
                                    if (mahasiswa != null) {
                                        fetchAndFilterClasses(nim, mahasiswa.jurusan)
                                    } else {
                                        val error =
                                            Resource.Error<List<Pair<String, List<Kelas>>>>("Data mahasiswa tidak ditemukan.")
                                        _originalGroupedClasses.value = error
                                        _groupedClasses.value = error
                                    }
                                }

                                is Resource.Error -> {
                                    val error =
                                        Resource.Error<List<Pair<String, List<Kelas>>>>(
                                            mahasiswaResource.message
                                                ?: "Gagal mendapatkan data mahasiswa.",
                                        )
                                    _originalGroupedClasses.value = error
                                    _groupedClasses.value = error
                                }

                                is Resource.Loading -> {
                                    // Already handled by initial Loading state
                                }
                            }
                        }
                    } else {
                        val error =
                            Resource.Error<List<Pair<String, List<Kelas>>>>("Pengguna bukan mahasiswa.")
                        _originalGroupedClasses.value = error
                        _groupedClasses.value = error
                    }
                } else {
                    val error =
                        Resource.Error<List<Pair<String, List<Kelas>>>>("ID pengguna tidak ditemukan.")
                    _originalGroupedClasses.value = error
                    _groupedClasses.value = error
                }
            } catch (e: Exception) {
                val error =
                    Resource.Error<List<Pair<String, List<Kelas>>>>(
                        e.message ?: "Terjadi kesalahan saat mengambil data kelas.",
                    )
                _originalGroupedClasses.value = error
                _groupedClasses.value = error
            }
        }
    }

    private fun fetchAndFilterClasses(
        nim: Int,
        jurusan: String,
    ) {
        viewModelScope.launch {
            val allClassesFlow = virtualClassUseCase.getAllKelasByJurusan(jurusan)
            val enrolledClassesFlow = virtualClassUseCase.getEnrolledClasses(nim)

            allClassesFlow
                .combine(enrolledClassesFlow) { allClassesResource, enrolledClassesResource ->
                    Pair(allClassesResource, enrolledClassesResource)
                }.collect { (allClassesResource, enrolledClassesResource) ->
                    when {
                        allClassesResource is Resource.Error -> {
                            val error =
                                Resource.Error<List<Pair<String, List<Kelas>>>>(
                                    allClassesResource.message ?: "Gagal memuat semua kelas.",
                                )
                            _originalGroupedClasses.value = error
                            _groupedClasses.value = error
                        }

                        enrolledClassesResource is Resource.Error -> {
                            val error =
                                Resource.Error<List<Pair<String, List<Kelas>>>>(
                                    enrolledClassesResource.message
                                        ?: "Gagal memuat kelas yang diikuti.",
                                )
                            _originalGroupedClasses.value = error
                            _groupedClasses.value = error
                        }

                        allClassesResource is Resource.Success && enrolledClassesResource is Resource.Success -> {
                            val allClasses = allClassesResource.data
                            val enrolledClasses = enrolledClassesResource.data

                            if (allClasses != null && enrolledClasses != null) {
                                val enrolledApprovedClassIds =
                                    enrolledClasses
                                        .filter { it.status == "approved" }
                                        .map { it.kelasId }
                                        .toSet()
                                val displayClasses =
                                    allClasses.filter { it.kelasId !in enrolledApprovedClassIds }

                                val grouped =
                                    displayClasses
                                        .groupBy { it.semester }
                                        .toList()
                                        .sortedBy { it.first }
                                val successResult = Resource.Success(grouped)
                                _originalGroupedClasses.value = successResult
                                filterClasses(searchQuery.value)

                                val allNidns = displayClasses.map { it.nidn }.distinct()
                                allNidns.forEach { nidn ->
                                    fetchDosenName(nidn)
                                }
                            } else {
                                val successEmpty =
                                    Resource.Success<List<Pair<String, List<Kelas>>>>(emptyList())
                                _originalGroupedClasses.value = successEmpty
                                _groupedClasses.value = successEmpty
                            }
                        }

                        allClassesResource is Resource.Loading || enrolledClassesResource is Resource.Loading -> {
                            _originalGroupedClasses.value = Resource.Loading()
                            _groupedClasses.value = Resource.Loading()
                        }
                    }
                }
        }
    }
}
