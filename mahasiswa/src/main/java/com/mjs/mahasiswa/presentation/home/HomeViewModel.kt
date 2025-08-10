package com.mjs.mahasiswa.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.model.Tugas
import com.mjs.core.domain.repository.IVirtualClassRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val virtualClassRepository: IVirtualClassRepository,
    private val appPreference: AppPreference,
) : ViewModel() {
    private val _mahasiswaData = MutableStateFlow<Resource<Mahasiswa>?>(null)
    val mahasiswaData: StateFlow<Resource<Mahasiswa>?> = _mahasiswaData

    private val _tugasListState = MutableStateFlow<Resource<List<Tugas>>?>(null)
    val tugasListState: StateFlow<Resource<List<Tugas>>?> = _tugasListState

    private val _enrolledCoursesMapState = MutableStateFlow<Map<String, String>>(emptyMap())
    val enrolledCoursesMapState: StateFlow<Map<String, String>> = _enrolledCoursesMapState

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

    private fun fetchAttendanceStreak() {
        viewModelScope.launch {
            _attendanceStreakState.value = Resource.Loading()
            val nim = appPreference.getLoggedInUserId().firstOrNull()

            if (nim == null) {
                _attendanceStreakState.value = Resource.Error("NIM pengguna tidak ditemukan.")
                return@launch
            }

            val enrolledClassesResource =
                virtualClassRepository
                    .getEnrolledClasses(nim)
                    .filter { it !is Resource.Loading }
                    .firstOrNull()

            if (enrolledClassesResource is Resource.Success) {
                val enrollments = enrolledClassesResource.data
                if (!enrollments.isNullOrEmpty()) {
                    val firstClassId = enrollments[0].kelasId
                    virtualClassRepository.getAttendanceStreak(nim, firstClassId).collect {
                        _attendanceStreakState.value = it
                    }
                } else {
                    _attendanceStreakState.value = Resource.Success(0)
                }
            } else if (enrolledClassesResource is Resource.Error) {
                _attendanceStreakState.value =
                    Resource.Error(
                        enrolledClassesResource.message
                            ?: "Gagal memuat kelas yang diikuti untuk streak.",
                    )
            } else {
                _attendanceStreakState.value =
                    Resource.Success(0)
            }
        }
    }

    private fun fetchTugasAndKelasMahasiswa() {
        viewModelScope.launch {
            _tugasListState.value = Resource.Loading()
            val nim = appPreference.getLoggedInUserId().firstOrNull()

            if (nim == null) {
                _tugasListState.value = Resource.Error("NIM pengguna tidak ditemukan.")
                return@launch
            }

            val enrolledClassesDeferred =
                async {
                    virtualClassRepository
                        .getEnrolledClasses(nim)
                        .filter { it !is Resource.Loading }
                        .firstOrNull()
                }
            val allKelasDeferred =
                async {
                    virtualClassRepository
                        .getAllKelas()
                        .filter { it !is Resource.Loading }
                        .firstOrNull()
                }

            val enrolledClassesResource = enrolledClassesDeferred.await()
            val allKelasResource = allKelasDeferred.await()

            if (enrolledClassesResource is Resource.Success && allKelasResource is Resource.Success) {
                val enrollments = enrolledClassesResource.data
                val allKelasList = allKelasResource.data

                if (enrollments.isNullOrEmpty()) {
                    _tugasListState.value = Resource.Success(emptyList())
                    _enrolledCoursesMapState.value = emptyMap()
                    return@launch
                }

                if (allKelasList.isNullOrEmpty()) {
                    _tugasListState.value =
                        Resource.Error("Gagal memuat detail kelas untuk mata kuliah yang diikuti.")
                    _enrolledCoursesMapState.value = emptyMap()
                    return@launch
                }

                val allKelasMap = allKelasList.associateBy { it.kelasId }

                _enrolledCoursesMapState.value =
                    enrollments
                        .mapNotNull { enrollment ->
                            allKelasMap[enrollment.kelasId]?.let { kelas ->
                                enrollment.kelasId to kelas.namaKelas
                            }
                        }.toMap()

                val allTugas = mutableListOf<Tugas>()
                var errorMessage: String? = null
                var hasError = false

                try {
                    val deferredTugasResources =
                        enrollments.map { enrollment ->
                            async {
                                virtualClassRepository
                                    .getAssignmentsByClass(enrollment.kelasId)
                                    .filter { it !is Resource.Loading }
                                    .firstOrNull()
                            }
                        }

                    val tugasResources = deferredTugasResources.awaitAll()

                    for ((_, resource) in tugasResources.withIndex()) {
                        when (resource) {
                            is Resource.Success -> {
                                resource.data?.let {
                                    allTugas.addAll(it)
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

                            null -> {
                                hasError = true
                                if (errorMessage == null) {
                                    errorMessage = "Menerima sumber daya null untuk tugas"
                                }
                            }
                        }
                    }

                    if (allTugas.isNotEmpty()) {
                        val distinctTugas = allTugas.distinctBy { it.assignmentId }
                        val dateFormat =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val currentDate = Date()
                        val activeTasks =
                            distinctTugas.filter {
                                try {
                                    val deadlineDate = dateFormat.parse(it.tanggalSelesai)
                                    deadlineDate != null && deadlineDate.after(currentDate)
                                } catch (_: Exception) {
                                    false
                                }
                            }
                        _tugasListState.value = Resource.Success(activeTasks)
                    } else if (hasError) {
                        _tugasListState.value =
                            Resource.Error(errorMessage ?: "Gagal memuat beberapa tugas.")
                    } else {
                        _tugasListState.value = Resource.Success(emptyList())
                    }
                } catch (ce: CancellationException) {
                    throw ce
                } catch (e: Exception) {
                    _tugasListState.value =
                        Resource.Error("Terjadi pengecualian saat mengambil tugas: ${e.message}")
                }
            } else if (enrolledClassesResource is Resource.Error) {
                _tugasListState.value =
                    Resource.Error(
                        enrolledClassesResource.message
                            ?: "Gagal memuat mata kuliah yang diikuti untuk tugas.",
                    )
            } else if (allKelasResource is Resource.Error) {
                _tugasListState.value =
                    Resource.Error(
                        allKelasResource.message
                            ?: "Gagal memuat informasi semua mata kuliah.",
                    )
            } else {
                val finalErrorMessage =
                    if (enrolledClassesResource == null || allKelasResource == null) {
                        "Data mata kuliah yang diikuti atau semua data mata kuliah adalah null setelah memfilter status pemuatan."
                    } else {
                        "Gagal mendapatkan informasi mata kuliah yang diikuti atau semua mata kuliah karena alasan yang tidak diketahui."
                    }
                _tugasListState.value = Resource.Error(finalErrorMessage)
            }
        }
    }

    private fun fetchTodaySchedule() {
        viewModelScope.launch {
            _todayScheduleState.value = Resource.Loading()
            val nim = appPreference.getLoggedInUserId().firstOrNull()
            if (nim != null) {
                virtualClassRepository.getTodaySchedule(nim).collect {
                    _todayScheduleState.value = it
                }
            } else {
                _todayScheduleState.value = Resource.Error("NIM pengguna tidak ditemukan.")
            }
        }
    }

    fun getClassNameById(kelasId: String): String = enrolledCoursesMapState.value[kelasId] ?: "Kelas Tidak Dikenal"
}
