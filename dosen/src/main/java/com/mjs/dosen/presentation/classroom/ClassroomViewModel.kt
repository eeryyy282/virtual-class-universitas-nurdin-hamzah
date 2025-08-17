package com.mjs.dosen.presentation.classroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class ClassroomViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val dosenClassesData: StateFlow<Resource<Pair<List<Pair<String, List<Kelas>>>, Int>>> =
        virtualClassUseCase
            .getLoggedInUserId()
            .flatMapLatest { nidn ->
                if (nidn == null) {
                    flowOf(Resource.Error("NIDN pengguna tidak ditemukan. Silakan masuk kembali."))
                } else {
                    virtualClassUseCase.getAllSchedulesByNidn(nidn).map { resource ->
                        when (resource) {
                            is Resource.Loading -> Resource.Loading()
                            is Resource.Error ->
                                Resource.Error(
                                    resource.message ?: "Gagal mengambil data kelas",
                                )

                            is Resource.Success -> {
                                val allClasses = resource.data ?: emptyList()
                                val totalClasses = allClasses.size
                                val grouped =
                                    allClasses
                                        .groupBy { it.semester }
                                        .map { Pair(it.key, it.value) }
                                Resource.Success(Pair(grouped, totalClasses))
                            }
                        }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )

    val groupedClasses: StateFlow<Resource<List<Pair<String, List<Kelas>>>>> =
        dosenClassesData
            .map { resource ->
                when (resource) {
                    is Resource.Loading -> Resource.Loading()
                    is Resource.Error ->
                        Resource.Error(
                            resource.message ?: "Gagal mengambil data kelas",
                        )

                    is Resource.Success -> {
                        val data = resource.data
                        if (data != null) {
                            Resource.Success(data.first)
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

    val totalClasses: StateFlow<Resource<Int>> =
        dosenClassesData
            .map { resource ->
                when (resource) {
                    is Resource.Loading -> Resource.Loading()
                    is Resource.Error ->
                        Resource.Error(
                            resource.message ?: "Gagal mengambil data kelas",
                        )

                    is Resource.Success -> {
                        val data = resource.data
                        if (data != null) {
                            Resource.Success(data.second)
                        } else {
                            Resource.Success(0)
                        }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )
}
