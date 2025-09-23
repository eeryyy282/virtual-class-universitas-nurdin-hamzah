package com.mjs.student.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Course
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
    val scheduleDetailsState: StateFlow<Resource<List<Pair<Course, String>>>> =
        virtualClassUseCase
            .getLoggedInUserId()
            .combine(virtualClassUseCase.getLoggedInUserType()) { studentId, userType ->
                Pair(studentId, userType)
            }.flatMapLatest { (studentId, userType) ->
                if (studentId != null && userType == VirtualClassUseCase.USER_TYPE_STUDENT) {
                    virtualClassUseCase
                        .getAllSchedulesByStudentId(studentId)
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
                                    val courseList = scheduleResource.data
                                    if (courseList.isNullOrEmpty()) {
                                        flowOf(Resource.Success(emptyList()))
                                    } else {
                                        val courseWithLecturerNameFlows =
                                            courseList.map { course ->
                                                virtualClassUseCase
                                                    .getLecturerByLecturerId(course.lecturerId)
                                                    .map { lecturerResource ->
                                                        val lecturerName =
                                                            when (lecturerResource) {
                                                                is Resource.Success ->
                                                                    lecturerResource.data?.name
                                                                        ?: course.lecturerId.toString()

                                                                is Resource.Error -> course.lecturerId.toString()
                                                                is Resource.Loading -> null
                                                            }
                                                        Pair(course, lecturerName)
                                                    }
                                            }
                                        combine(courseWithLecturerNameFlows) { results ->
                                            val successfullyResolved =
                                                results.mapNotNull { (course, lecturerName) ->
                                                    lecturerName?.let { Pair(course, it) }
                                                }
                                            Resource.Success(successfullyResolved)
                                        }
                                    }
                                }
                            }
                        }
                } else if (studentId == null) {
                    flowOf(Resource.Error("Pengguna tidak login atau NIM tidak ditemukan."))
                } else {
                    flowOf(Resource.Error("Pengguna bukan mahasiswa."))
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )

    val scheduleListState: StateFlow<Resource<List<Course>>> =
        scheduleDetailsState
            .map { resource ->
                when (resource) {
                    is Resource.Loading -> Resource.Loading()
                    is Resource.Error ->
                        Resource.Error(
                            resource.message ?: "Gagal memproses daftar jadwal",
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

    val lecturerNamesMap: StateFlow<Map<Long, String>> =
        scheduleDetailsState
            .map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val data = resource.data
                        data?.associate { (course, lecturerName) -> course.lecturerId to lecturerName }
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
