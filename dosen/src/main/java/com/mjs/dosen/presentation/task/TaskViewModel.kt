package com.mjs.dosen.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Tugas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    @Suppress("ktlint:standard:backing-property-naming")
    private val _allKelasMap = MutableStateFlow<Map<String, String>>(emptyMap())

    val tasks: StateFlow<Resource<Pair<List<Tugas>, List<Tugas>>>> =
        appPreference
            .getLoggedInUserId()
            .flatMapLatest { nidnResource ->
                val nidn = nidnResource
                if (nidn == null) {
                    MutableStateFlow(Resource.Error("NIDN dosen tidak ditemukan."))
                } else {
                    virtualClassUseCase.getAllKelas().flatMapLatest { allClassesResource ->
                        when (allClassesResource) {
                            is Resource.Loading -> MutableStateFlow(Resource.Loading())
                            is Resource.Error ->
                                MutableStateFlow(
                                    Resource.Error(
                                        allClassesResource.message
                                            ?: "Gagal memuat detail semua kelas.",
                                    ),
                                )

                            is Resource.Success -> {
                                _allKelasMap.value = allClassesResource.data?.associate {
                                    it.kelasId to it.namaKelas
                                } ?: emptyMap()

                                val activeTasksFlow =
                                    virtualClassUseCase.getActiveAssignmentsForDosen(nidn)
                                val pastDeadlineTasksFlow =
                                    virtualClassUseCase.getPastDeadlineAssignmentsForDosen(nidn)

                                combine(
                                    activeTasksFlow,
                                    pastDeadlineTasksFlow,
                                ) { activeResult, pastResult ->
                                    processTaskResults(activeResult, pastResult)
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

    private fun processTaskResults(
        activeResult: Resource<List<Tugas>>,
        pastResult: Resource<List<Tugas>>,
    ): Resource<Pair<List<Tugas>, List<Tugas>>> {
        val activeTasks = mutableListOf<Tugas>()
        val pastDeadlineTasks = mutableListOf<Tugas>()
        var firstError: String? = null
        var isLoading = false

        when (activeResult) {
            is Resource.Success -> activeTasks.addAll(activeResult.data ?: emptyList())
            is Resource.Error -> firstError = activeResult.message
            is Resource.Loading -> isLoading = true
        }

        when (pastResult) {
            is Resource.Success -> pastDeadlineTasks.addAll(pastResult.data ?: emptyList())
            is Resource.Error -> if (firstError == null) firstError = pastResult.message
            is Resource.Loading -> isLoading = true
        }

        return when {
            firstError != null -> Resource.Error(firstError)
            isLoading -> Resource.Loading()
            else -> Resource.Success(Pair(activeTasks, pastDeadlineTasks))
        }
    }

    fun getClassNameById(kelasId: String): String = _allKelasMap.value[kelasId] ?: "Kelas Tidak Dikenal"
}
