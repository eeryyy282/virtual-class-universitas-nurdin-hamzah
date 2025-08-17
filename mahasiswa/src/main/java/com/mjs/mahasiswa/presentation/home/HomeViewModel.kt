package com.mjs.mahasiswa.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.model.Tugas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

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
                                            val approvedEnrollments =
                                                enrollments.filter { it.status == "approved" }
                                            if (approvedEnrollments.isNotEmpty()) {
                                                val firstKelasId = approvedEnrollments[0].kelasId
                                                virtualClassUseCase.getAttendanceStreak(
                                                    nim,
                                                    firstKelasId,
                                                )
                                            } else {
                                                flowOf(Resource.Success(0))
                                            }
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
                .flatMapLatest { loggedInNim ->
                    if (loggedInNim == null) {
                        flowOf(Resource.Error("NIM pengguna tidak ditemukan."))
                    } else {
                        val enrolledClassesFlow: Flow<Resource<List<EnrollmentEntity>>> =
                            virtualClassUseCase.getEnrolledClasses(loggedInNim)
                        val allKelasFlow: Flow<Resource<List<Kelas>>> =
                            virtualClassUseCase.getAllKelas()

                        combine(
                            enrolledClassesFlow,
                            allKelasFlow,
                        ) { enrolledClassesResource, allKelasResource ->
                            Pair(
                                enrolledClassesResource,
                                allKelasResource,
                            )
                        }.flatMapLatest { (enrolledClassesResource, allKelasResource) ->
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
                                    val approvedEnrollments =
                                        enrollments.filter { it.status == "approved" }
                                    val allKelasMap = allKelasList.associateBy { it.kelasId }
                                    _enrolledCoursesMapState.value =
                                        approvedEnrollments
                                            .mapNotNull { enrollment ->
                                                allKelasMap[enrollment.kelasId]?.let {
                                                    enrollment.kelasId to
                                                        Pair(
                                                            it.namaKelas,
                                                            it.classImage,
                                                        )
                                                }
                                            }.toMap()

                                    if (approvedEnrollments.isEmpty()) {
                                        flowOf(Resource.Success(emptyList()))
                                    } else {
                                        val assignmentFlows: List<Flow<Resource<List<Tugas>>>> =
                                            approvedEnrollments.map { enrollment ->
                                                virtualClassUseCase.getNotFinishedTasks(
                                                    loggedInNim,
                                                    enrollment.kelasId,
                                                )
                                            }

                                        combine(assignmentFlows) { results: Array<Resource<List<Tugas>>> ->
                                            val allTasks = mutableListOf<Tugas>()
                                            var firstError: String? = null
                                            var isLoadingCombined = false

                                            results.forEach { resource ->
                                                when (resource) {
                                                    is Resource.Success ->
                                                        allTasks.addAll(
                                                            resource.data ?: emptyList(),
                                                        )

                                                    is Resource.Error ->
                                                        if (firstError == null) {
                                                            firstError =
                                                                resource.message
                                                        }

                                                    is Resource.Loading -> isLoadingCombined = true
                                                }
                                            }
                                            when {
                                                firstError != null ->
                                                    Resource.Error(
                                                        firstError,
                                                    )

                                                isLoadingCombined && allTasks.isEmpty() -> Resource.Loading()
                                                else -> Resource.Success(allTasks.distinctBy { it.assignmentId })
                                            }
                                        }
                                    }
                                }
                            } else if (enrolledClassesResource is Resource.Error) {
                                flowOf(
                                    Resource.Error(
                                        enrolledClassesResource.message
                                            ?: "Gagal memuat kelas yang diikuti.",
                                    ),
                                )
                            } else if (allKelasResource is Resource.Error) {
                                flowOf(
                                    Resource.Error(
                                        allKelasResource.message
                                            ?: "Gagal memuat semua data kelas.",
                                    ),
                                )
                            } else {
                                flowOf(Resource.Loading())
                            }
                        }
                    }
                }.collectLatest { resource: Resource<List<Tugas>> ->
                    _tugasListState.value = resource
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
                    if (nim == null) {
                        flowOf(Resource.Error("NIM pengguna tidak ditemukan."))
                    } else {
                        virtualClassUseCase.getTodaySchedule(nim)
                    }
                }.collectLatest {
                    _todayScheduleState.value = it
                }
        }
    }

    fun getClassNameById(kelasId: String): String = enrolledCoursesMapState.value[kelasId]?.first ?: "Kelas Tidak Dikenal"

    fun getClassPhotoProfileById(kelasId: String): String? = enrolledCoursesMapState.value[kelasId]?.second
}
