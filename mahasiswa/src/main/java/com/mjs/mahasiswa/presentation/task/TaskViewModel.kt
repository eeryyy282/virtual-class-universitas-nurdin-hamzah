package com.mjs.mahasiswa.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Tugas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
    appPreference: AppPreference,
) : ViewModel() {
    private val _enrolledCoursesMapState = MutableStateFlow<Map<String, String>>(emptyMap())
    val enrolledCoursesMapState: StateFlow<Map<String, String>> = _enrolledCoursesMapState

    val tasks: StateFlow<Resource<Pair<List<Tugas>, List<Tugas>>>> =
        appPreference
            .getLoggedInUserId()
            .flatMapLatest { nimResource ->
                val nim = nimResource
                if (nim == null) {
                    MutableStateFlow(Resource.Error("NIM pengguna tidak ditemukan."))
                } else {
                    loadEnrolledClassesAndAllKelasDetails(nim).flatMapLatest { enrolledClassesDetailsResult ->
                        when (enrolledClassesDetailsResult) {
                            is Resource.Loading -> MutableStateFlow(Resource.Loading())
                            is Resource.Error ->
                                MutableStateFlow(
                                    Resource.Error(
                                        enrolledClassesDetailsResult.message
                                            ?: "Gagal memuat detail kelas.",
                                    ),
                                )

                            is Resource.Success -> {
                                val enrolledKelasIds =
                                    enrolledClassesDetailsResult.data?.first ?: emptyList()
                                val allKelasMap =
                                    enrolledClassesDetailsResult.data?.second ?: emptyMap()
                                _enrolledCoursesMapState.value = allKelasMap

                                if (enrolledKelasIds.isEmpty()) {
                                    MutableStateFlow(
                                        Resource.Success(
                                            Pair(
                                                emptyList(),
                                                emptyList(),
                                            ),
                                        ),
                                    )
                                } else {
                                    val notFinishedTasksFlows =
                                        enrolledKelasIds.map { kelasId ->
                                            virtualClassUseCase.getNotFinishedTasks(nim, kelasId)
                                        }
                                    val lateTasksFlows =
                                        enrolledKelasIds.map { kelasId ->
                                            virtualClassUseCase.getLateTasks(nim, kelasId)
                                        }

                                    combine(notFinishedTasksFlows) { results -> results.toList() }
                                        .combine(
                                            combine(lateTasksFlows) { results -> results.toList() },
                                        ) { notFinishedResults, lateResults ->
                                            processTaskResults(notFinishedResults, lateResults)
                                        }
                                }
                            }
                        }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )

    private fun loadEnrolledClassesAndAllKelasDetails(nim: Int): Flow<Resource<Pair<List<String>, Map<String, String>>>> =
        virtualClassUseCase
            .getEnrolledClasses(nim)
            .combine(virtualClassUseCase.getAllKelas()) { enrolledClassesResource, allKelasResource ->
                if (enrolledClassesResource is Resource.Success && allKelasResource is Resource.Success) {
                    val enrolledKelasIds =
                        enrolledClassesResource.data?.map { it.kelasId } ?: emptyList()
                    val allKelasMap =
                        allKelasResource.data?.associate { it.kelasId to it.namaKelas }
                            ?: emptyMap()
                    Resource.Success(Pair(enrolledKelasIds, allKelasMap))
                } else if (enrolledClassesResource is Resource.Error) {
                    Resource.Error(
                        enrolledClassesResource.message ?: "Gagal memuat kelas yang diikuti.",
                    )
                } else if (allKelasResource is Resource.Error) {
                    Resource.Error(allKelasResource.message ?: "Gagal memuat semua data kelas.")
                } else {
                    Resource.Loading()
                }
            }

    private fun processTaskResults(
        notFinishedResults: List<Resource<List<Tugas>>>,
        lateResults: List<Resource<List<Tugas>>>,
    ): Resource<Pair<List<Tugas>, List<Tugas>>> {
        val allNotFinishedTasks = mutableListOf<Tugas>()
        val allLateTasks = mutableListOf<Tugas>()
        var firstError: String? = null
        var isLoading = false

        notFinishedResults.forEach {
            when (it) {
                is Resource.Success -> allNotFinishedTasks.addAll(it.data ?: emptyList())
                is Resource.Error -> if (firstError == null) firstError = it.message
                is Resource.Loading -> isLoading = true
            }
        }

        lateResults.forEach {
            when (it) {
                is Resource.Success -> allLateTasks.addAll(it.data ?: emptyList())
                is Resource.Error -> if (firstError == null) firstError = it.message
                is Resource.Loading -> isLoading = true
            }
        }

        return when {
            firstError != null -> Resource.Error(firstError)
            isLoading -> Resource.Loading()
            else ->
                Resource.Success(
                    Pair(
                        allNotFinishedTasks.distinctBy { it.assignmentId },
                        allLateTasks.distinctBy { it.assignmentId },
                    ),
                )
        }
    }

    fun getClassNameById(kelasId: String): String = enrolledCoursesMapState.value[kelasId] ?: "Kelas Tidak Dikenal"
}
