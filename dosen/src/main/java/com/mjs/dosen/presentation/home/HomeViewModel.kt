package com.mjs.dosen.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Dosen
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.model.Tugas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    private val _dosenData = MutableStateFlow<Resource<Dosen>?>(null)
    val dosenData: StateFlow<Resource<Dosen>?> = _dosenData

    private val _tugasListDosenState = MutableStateFlow<Resource<List<Tugas>>?>(null)
    val tugasListDosenState: StateFlow<Resource<List<Tugas>>?> = _tugasListDosenState

    private val _kelasDosenMapState =
        MutableStateFlow<Map<String, Pair<String, String?>>>(emptyMap())
    val kelasDosenMapState: StateFlow<Map<String, Pair<String, String?>>> = _kelasDosenMapState

    private val _todayScheduleState = MutableStateFlow<Resource<List<Kelas>>?>(null)
    val todayScheduleState: StateFlow<Resource<List<Kelas>>?> = _todayScheduleState

    init {
        fetchDosenData()
        fetchTugasUntukDosen()
        fetchTodaySchedule()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchDosenData() {
        viewModelScope.launch {
            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { nidn ->
                    if (nidn != null) {
                        virtualClassUseCase.getLoggedInUserType().flatMapLatest { userType ->
                            if (userType == VirtualClassUseCase.USER_TYPE_DOSEN) {
                                virtualClassUseCase.getDosenByNidn(nidn)
                            } else {
                                flowOf(Resource.Error("User is not a Dosen"))
                            }
                        }
                    } else {
                        flowOf(Resource.Error("NIDN not found"))
                    }
                }.collectLatest {
                    _dosenData.value = it
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchTugasUntukDosen() {
        viewModelScope.launch {
            _tugasListDosenState.value = Resource.Loading()

            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { nidn ->
                    if (nidn == null) {
                        flowOf(Resource.Error("NIDN dosen tidak ditemukan atau tidak valid."))
                    } else {
                        virtualClassUseCase.getAllKelas().flatMapLatest { allKelasResource ->
                            when (allKelasResource) {
                                is Resource.Success -> {
                                    val semuaKelas = allKelasResource.data ?: emptyList()
                                    val kelasYangDiajar = semuaKelas.filter { it.nidn == nidn }

                                    if (kelasYangDiajar.isEmpty()) {
                                        _kelasDosenMapState.value = emptyMap()
                                        flowOf(Resource.Success(emptyList()))
                                    } else {
                                        _kelasDosenMapState.value =
                                            kelasYangDiajar.associate {
                                                it.kelasId to Pair(it.namaKelas, it.classImage)
                                            }

                                        val assignmentFlows =
                                            kelasYangDiajar.map { kelas ->
                                                virtualClassUseCase.getAssignmentsByClass(kelas.kelasId)
                                            }

                                        combine(assignmentFlows) { resources ->
                                            val allTugas = mutableListOf<Tugas>()
                                            var hasError = false
                                            var errorMessage: String? = null

                                            resources.forEach { resource ->
                                                when (resource) {
                                                    is Resource.Success -> {
                                                        resource.data?.let { tugasList ->
                                                            allTugas.addAll(tugasList)
                                                        }
                                                    }

                                                    is Resource.Error -> {
                                                        hasError = true
                                                        if (errorMessage == null) {
                                                            errorMessage = resource.message
                                                        }
                                                    }

                                                    is Resource.Loading -> {
                                                    }
                                                }
                                            }

                                            if (allTugas.isNotEmpty()) {
                                                val distinctTugas =
                                                    allTugas.distinctBy { it.assignmentId }
                                                val dateFormat =
                                                    SimpleDateFormat(
                                                        "yyyy-MM-dd HH:mm:ss",
                                                        Locale.getDefault(),
                                                    )
                                                val currentDate = Date()
                                                val activeTasks =
                                                    distinctTugas.filter {
                                                        try {
                                                            val deadlineDate =
                                                                dateFormat.parse(it.tanggalSelesai)
                                                            deadlineDate != null &&
                                                                deadlineDate.after(
                                                                    currentDate,
                                                                )
                                                        } catch (_: Exception) {
                                                            false
                                                        }
                                                    }
                                                Resource.Success(activeTasks)
                                            } else if (hasError) {
                                                Resource.Error(
                                                    errorMessage
                                                        ?: "Gagal memuat tugas untuk kelas dosen.",
                                                )
                                            } else {
                                                Resource.Success(emptyList())
                                            }
                                        }
                                    }
                                }

                                is Resource.Error -> {
                                    flowOf(
                                        Resource.Error(
                                            allKelasResource.message
                                                ?: "Gagal memuat daftar semua kelas.",
                                        ),
                                    )
                                }

                                is Resource.Loading -> {
                                    flowOf(Resource.Loading())
                                }
                            }
                        }
                    }
                }.collectLatest {
                    _tugasListDosenState.value = it
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchTodaySchedule() {
        viewModelScope.launch {
            _todayScheduleState.value = Resource.Loading()
            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { nidn ->
                    if (nidn != null) {
                        virtualClassUseCase.getTodayScheduleDosen(nidn)
                    } else {
                        flowOf(Resource.Error("NIDN pengguna tidak ditemukan atau tidak valid."))
                    }
                }.collectLatest {
                    _todayScheduleState.value = it
                }
        }
    }

    fun getNamaKelasById(kelasId: String): String = _kelasDosenMapState.value[kelasId]?.first ?: "Kelas Tidak Dikenal"

    fun getKelasImageById(kelasId: String): String? = _kelasDosenMapState.value[kelasId]?.second
}
