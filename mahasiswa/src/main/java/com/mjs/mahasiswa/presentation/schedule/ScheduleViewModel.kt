package com.mjs.mahasiswa.presentation.schedule

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
class ScheduleViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val scheduleWithDosenNames: StateFlow<Resource<List<Pair<Kelas, String>>>> =
        virtualClassUseCase
            .getLoggedInUserId()
            .combine(virtualClassUseCase.getLoggedInUserType()) { nim, userType ->
                Pair(nim, userType)
            }.flatMapLatest { (nim, userType) ->
                if (nim != null && userType == VirtualClassUseCase.USER_TYPE_MAHASISWA) {
                    virtualClassUseCase
                        .getAllSchedulesByNim(nim)
                        .flatMapLatest { scheduleResource ->
                            when (scheduleResource) {
                                is Resource.Loading -> flowOf(Resource.Loading())
                                is Resource.Error ->
                                    flowOf(
                                        Resource.Error(
                                            scheduleResource.message
                                                ?: "Gagal mengambil jadwal mahasiswa",
                                        ),
                                    )

                                is Resource.Success -> {
                                    val kelasList = scheduleResource.data
                                    if (kelasList.isNullOrEmpty()) {
                                        flowOf(Resource.Success(emptyList()))
                                    } else {
                                        val kelasWithDosenNameFlows =
                                            kelasList.map { kelas ->
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
                                        combine(kelasWithDosenNameFlows) { results ->
                                            val successfullyResolved =
                                                results.mapNotNull { (kelas, dosenName) ->
                                                    dosenName?.let { Pair(kelas, it) }
                                                }
                                            Resource.Success(successfullyResolved)
                                        }
                                    }
                                }
                            }
                        }
                } else if (nim == null) {
                    flowOf(Resource.Error("User tidak melakukan login atau NIM tidak ditemukan."))
                } else {
                    flowOf(Resource.Error("Pengguna bukan mahasiswa."))
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )

    val scheduleForUi: StateFlow<Resource<List<Kelas>>> =
        scheduleWithDosenNames
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
        scheduleWithDosenNames
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
