package com.mjs.mahasiswa.presentation.classroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class ClassroomViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    private val enrolledClassesRaw: StateFlow<Resource<List<Kelas>>> =
        virtualClassUseCase
            .getLoggedInUserId()
            .flatMapLatest { nim ->
                if (nim == null) {
                    flowOf(Resource.Error("NIM pengguna tidak ditemukan. Silakan masuk kembali."))
                } else {
                    virtualClassUseCase.getAllSchedulesByNim(nim)
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )

    val enrolledClassesWithDosenNames: StateFlow<Resource<List<Pair<Kelas, String>>>> =
        enrolledClassesRaw
            .flatMapLatest { classesResource ->
                when (classesResource) {
                    is Resource.Loading -> flowOf(Resource.Loading())
                    is Resource.Error ->
                        flowOf(
                            Resource.Error(
                                classesResource.message
                                    ?: "Gagal mengambil data kelas yang diikuti",
                            ),
                        )

                    is Resource.Success -> {
                        val classes = classesResource.data
                        if (classes.isNullOrEmpty()) {
                            flowOf(Resource.Success(emptyList()))
                        } else {
                            val dosenNameFlows =
                                classes.map { kelas ->
                                    virtualClassUseCase
                                        .getDosenByNidn(kelas.nidn)
                                        .map { dosenResource ->
                                            val dosenName =
                                                when (dosenResource) {
                                                    is Resource.Success ->
                                                        dosenResource.data?.nama
                                                            ?: kelas.nidn.toString()

                                                    is Resource.Error -> kelas.nidn.toString()
                                                    is Resource.Loading -> null
                                                }
                                            Pair(kelas, dosenName)
                                        }
                                }

                            combine(dosenNameFlows) { kelasWithMaybeDosenNameList ->
                                val resultList =
                                    kelasWithMaybeDosenNameList.mapNotNull { (kelas, dosenName) ->
                                        dosenName?.let { Pair(kelas, it) }
                                    }
                                Resource.Success(resultList)
                            }
                        }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )

    val enrolledClassesForUi: StateFlow<Resource<List<Kelas>>> =
        enrolledClassesWithDosenNames
            .map { resource ->
                when (resource) {
                    is Resource.Loading -> Resource.Loading()
                    is Resource.Error ->
                        Resource.Error(
                            resource.message ?: "Error memproses list jadwal",
                        )

                    is Resource.Success -> {
                        val data = resource.data
                        if (data != null) {
                            Resource.Success(data.map { it.first })
                        } else {
                            Resource.Success(emptyList())
                        }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )

    val dosenNamesMap: StateFlow<Map<Int, String>> =
        enrolledClassesWithDosenNames
            .map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val data = resource.data
                        data?.associate { (kelas, dosenName) -> kelas.nidn to dosenName }
                            ?: emptyMap()
                    }

                    else -> emptyMap()
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap(),
            )
}
