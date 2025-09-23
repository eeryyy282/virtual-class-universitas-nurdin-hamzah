package com.mjs.student.presentation.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class CourseViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    @Suppress("ktlint:standard:backing-property-naming")
    private val _refreshTrigger = MutableStateFlow(0)

    fun triggerRefresh() {
        _refreshTrigger.value++
    }

    private val enrolledCoursesRaw: StateFlow<Resource<List<Course>>> =
        _refreshTrigger
            .flatMapLatest { _ ->
                virtualClassUseCase
                    .getLoggedInUserId()
                    .flatMapLatest { studentId ->
                        if (studentId == null) {
                            flowOf(Resource.Error("NIM pengguna tidak ditemukan. Silakan masuk kembali."))
                        } else {
                            virtualClassUseCase.getAllSchedulesByStudentId(studentId)
                        }
                    }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )

    val enrolledCoursesWithLecturerNames: StateFlow<Resource<List<Pair<Course, String>>>> =
        enrolledCoursesRaw
            .flatMapLatest { coursesResource ->
                when (coursesResource) {
                    is Resource.Loading -> flowOf(Resource.Loading())
                    is Resource.Error ->
                        flowOf(
                            Resource.Error(
                                coursesResource.message
                                    ?: "Gagal mengambil data mata kuliah yang diikuti",
                            ),
                        )

                    is Resource.Success -> {
                        val courses = coursesResource.data
                        if (courses.isNullOrEmpty()) {
                            flowOf(Resource.Success(emptyList()))
                        } else {
                            val lecturerNameFlows =
                                courses.map { course ->
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

                            combine(lecturerNameFlows) { courseWithMaybeLecturerNameList ->
                                val resultList =
                                    courseWithMaybeLecturerNameList.mapNotNull { (course, lecturerName) ->
                                        lecturerName?.let { Pair(course, it) }
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

    val enrolledCourseListState: StateFlow<Resource<List<Course>>> =
        enrolledCoursesWithLecturerNames
            .map { resource ->
                when (resource) {
                    is Resource.Loading -> Resource.Loading()
                    is Resource.Error ->
                        Resource.Error(
                            resource.message ?: "Gagal memproses daftar mata kuliah",
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
        enrolledCoursesWithLecturerNames
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
