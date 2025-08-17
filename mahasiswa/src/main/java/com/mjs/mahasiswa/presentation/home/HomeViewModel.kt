package com.mjs.mahasiswa.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.model.Mahasiswa
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
    private val _mahasiswaData = MutableStateFlow<Resource<Mahasiswa>?>(null)
    val mahasiswaData: StateFlow<Resource<Mahasiswa>?> = _mahasiswaData

    private val _tugasListState = MutableStateFlow<Resource<List<Tugas>>?>(null)
    val tugasListState: StateFlow<Resource<List<Tugas>>?> = _tugasListState

    private val _enrolledCoursesMapState =
        MutableStateFlow<Map<String, Pair<String, String?>>>(emptyMap())
    val enrolledCoursesMapState: StateFlow<Map<String, Pair<String, String?>>> =
        _enrolledCoursesMapState

    private val _attendanceStreakState = MutableStateFlow<Resource<Int>?>(null)
    val attendanceStreakState: StateFlow<Resource<Int>?> = _attendanceStreakState

    private val _todayScheduleState = MutableStateFlow<Resource<List<Kelas>>?>(null)
    val todayScheduleState: StateFlow<Resource<List<Kelas>>?> = _todayScheduleState

    init {
        fetchMahasiswaData()
        fetchTugasAndKelasMahasiswa()
        fetchAttendanceStreak()
        fetchTodaySchedule()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchMahasiswaData() {
        viewModelScope.launch {
            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { nim ->
                    if (nim != null) {
                        virtualClassUseCase.getMahasiswaByNim(nim)
                    } else {
                        flowOf(Resource.Error("NIM tidak ditemukan"))
                    }
                }.collectLatest {
                    _mahasiswaData.value = it
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchAttendanceStreak() {
        viewModelScope.launch {
            _attendanceStreakState.value = Resource.Loading()
            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { nim ->
                    if (nim == null) {
                        flowOf(Resource.Error("NIM pengguna tidak ditemukan."))
                    } else {
                        virtualClassUseCase
                            .getEnrolledClasses(nim)
                            .flatMapLatest { enrolledClassesResource ->
                                when (enrolledClassesResource) {
                                    is Resource.Success -> {
                                        val enrollments = enrolledClassesResource.data
                                        if (!enrollments.isNullOrEmpty()) {
                                            val firstKelasId = enrollments[0].kelasId
                                            virtualClassUseCase.getAttendanceStreak(
                                                nim,
                                                firstKelasId,
                                            )
                                        } else {
                                            flowOf(Resource.Success(0))
                                        }
                                    }

                                    is Resource.Error -> {
                                        flowOf(
                                            Resource.Error(
                                                enrolledClassesResource.message
                                                    ?: "Gagal memuat kelas yang diikuti untuk mengambil streak.",
                                            ),
                                        )
                                    }

                                    is Resource.Loading -> flowOf(Resource.Loading())
                                }
                            }
                    }
                }.collectLatest {
                    _attendanceStreakState.value = it
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchTugasAndKelasMahasiswa() {
        viewModelScope.launch {
            _tugasListState.value = Resource.Loading()

            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { nim ->
                    if (nim == null) {
                        flowOf(Resource.Error("NIM pengguna tidak ditemukan."))
                    } else {
                        val enrolledClassesFlow = virtualClassUseCase.getEnrolledClasses(nim)
                        val allKelasFlow = virtualClassUseCase.getAllKelas()

                        combine(
                            enrolledClassesFlow,
                            allKelasFlow,
                        ) { enrolledClassesResource, allKelasResource ->
                            Triple(enrolledClassesResource, allKelasResource, nim)
                        }.flatMapLatest { (enrolledClassesResource, allKelasResource, _) ->
                            if (enrolledClassesResource is Resource.Success && allKelasResource is Resource.Success) {
                                val enrollments = enrolledClassesResource.data
                                val allKelasList = allKelasResource.data

                                if (enrollments.isNullOrEmpty()) {
                                    _enrolledCoursesMapState.value = emptyMap()
                                    flowOf(Resource.Success(emptyList()))
                                } else if (allKelasList.isNullOrEmpty()) {
                                    _enrolledCoursesMapState.value = emptyMap()
                                    flowOf(
                                        Resource.Error("Gagal memuat detail kelas untuk mata kuliah yang diikuti."),
                                    )
                                } else {
                                    val allKelasMap = allKelasList.associateBy { it.kelasId }
                                    _enrolledCoursesMapState.value =
                                        enrollments
                                            .mapNotNull { enrollment ->
                                                allKelasMap[enrollment.kelasId]?.let {
                                                    enrollment.kelasId to
                                                        Pair(
                                                            it.namaKelas,
                                                            it.classImage,
                                                        )
                                                }
                                            }.toMap()

                                    if (enrollments.isEmpty()) {
                                        flowOf(Resource.Success(emptyList()))
                                    } else {
                                        val assignmentFlows =
                                            enrollments.map { enrollment ->
                                                virtualClassUseCase.getAssignmentsByClass(enrollment.kelasId)
                                            }

                                        combine(assignmentFlows) { resources ->
                                            val allTugas = mutableListOf<Tugas>()
                                            var hasError = false
                                            var errorMessage: String? = null

                                            resources.forEach { resource ->
                                                when (resource) {
                                                    is Resource.Success ->
                                                        resource.data?.let {
                                                            allTugas.addAll(
                                                                it,
                                                            )
                                                        }

                                                    is Resource.Error -> {
                                                        hasError = true
                                                        if (errorMessage == null) {
                                                            errorMessage =
                                                                resource.message
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
                                                    errorMessage ?: "Gagal memuat beberapa tugas.",
                                                )
                                            } else {
                                                Resource.Success(emptyList())
                                            }
                                        }
                                    }
                                }
                            } else if (enrolledClassesResource is Resource.Error) {
                                flowOf(
                                    Resource.Error(
                                        enrolledClassesResource.message
                                            ?: "Gagal memuat mata kuliah yang diikuti untuk tugas.",
                                    ),
                                )
                            } else if (allKelasResource is Resource.Error) {
                                flowOf(
                                    Resource.Error(
                                        allKelasResource.message
                                            ?: "Gagal memuat informasi semua mata kuliah.",
                                    ),
                                )
                            } else {
                                flowOf(Resource.Loading())
                            }
                        }
                    }
                }.collectLatest {
                    _tugasListState.value = it
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchTodaySchedule() {
        viewModelScope.launch {
            _todayScheduleState.value = Resource.Loading()
            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { nim ->
                    if (nim != null) {
                        virtualClassUseCase.getTodaySchedule(nim)
                    } else {
                        flowOf(Resource.Error("NIM pengguna tidak ditemukan."))
                    }
                }.collectLatest {
                    _todayScheduleState.value = it
                }
        }
    }

    fun getClassNameById(kelasId: String): String = enrolledCoursesMapState.value[kelasId]?.first ?: "Kelas Tidak Dikenal"

    fun getClassPhotoProfileById(kelasId: String): String? = enrolledCoursesMapState.value[kelasId]?.second
}
