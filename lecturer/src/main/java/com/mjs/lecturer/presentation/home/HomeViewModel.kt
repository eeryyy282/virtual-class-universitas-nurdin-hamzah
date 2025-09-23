package com.mjs.lecturer.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Assignment
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.model.Lecturer
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import com.mjs.lecturer.utils.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val _lecturerData = MutableStateFlow<Resource<Lecturer>?>(null)
    val lecturerData: StateFlow<Resource<Lecturer>?> = _lecturerData

    private val _assignmentListLecturerState = MutableStateFlow<Resource<List<Assignment>>?>(null)
    val assignmentListLecturerState: StateFlow<Resource<List<Assignment>>?> =
        _assignmentListLecturerState

    private val _courseLecturerMapState =
        MutableStateFlow<Map<String, Pair<String, String?>>>(emptyMap())
    val courseLecturerMapState: StateFlow<Map<String, Pair<String, String?>>> =
        _courseLecturerMapState

    private val _todayScheduleState = MutableStateFlow<Resource<List<Course>>?>(null)
    val todayScheduleState: StateFlow<Resource<List<Course>>?> = _todayScheduleState

    init {
        fetchLecturerData()
        fetchAssignmentsForLecturer()
        fetchTodaySchedule()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchLecturerData() {
        viewModelScope.launch {
            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { lecturerId ->
                    if (lecturerId != null) {
                        virtualClassUseCase.getLoggedInUserType().flatMapLatest { userType ->
                            if (userType == VirtualClassUseCase.USER_TYPE_LECTURER) {
                                virtualClassUseCase.getLecturerByLecturerId(lecturerId)
                            } else {
                                flowOf(Resource.Error("Pengguna bukan Dosen"))
                            }
                        }
                    } else {
                        flowOf(Resource.Error("ID Dosen tidak ditemukan"))
                    }
                }.collectLatest {
                    _lecturerData.value = it
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchAssignmentsForLecturer() {
        viewModelScope.launch {
            _assignmentListLecturerState.value = Resource.Loading()

            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { lecturerId ->
                    if (lecturerId == null) {
                        flowOf(Resource.Error("ID Dosen tidak ditemukan atau tidak valid."))
                    } else {
                        virtualClassUseCase.getAllCourse().flatMapLatest { allCoursesResource ->
                            when (allCoursesResource) {
                                is Resource.Success -> {
                                    val allCourses = allCoursesResource.data ?: emptyList()
                                    val coursesTaught =
                                        allCourses.filter { it.lecturerId == lecturerId }

                                    if (coursesTaught.isEmpty()) {
                                        _courseLecturerMapState.value = emptyMap()
                                        flowOf(Resource.Success(emptyList()))
                                    } else {
                                        _courseLecturerMapState.value =
                                            coursesTaught.associate {
                                                it.courseId to Pair(it.courseName, it.courseImage)
                                            }

                                        val assignmentFlows =
                                            coursesTaught.map { course ->
                                                virtualClassUseCase.getAssignmentsByCourse(course.courseId)
                                            }

                                        combine(assignmentFlows) { resources ->
                                            val allAssignments = mutableListOf<Assignment>()
                                            var hasError = false
                                            var errorMessage: String? = null

                                            resources.forEach { resource ->
                                                when (resource) {
                                                    is Resource.Success -> {
                                                        resource.data?.let { assignmentsFromResource ->
                                                            allAssignments.addAll(
                                                                assignmentsFromResource,
                                                            )
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
                                                }
                                            }

                                            if (allAssignments.isNotEmpty()) {
                                                val distinctAssignments =
                                                    allAssignments.distinctBy { it.assignmentId }
                                                val activeAssignments =
                                                    distinctAssignments.filter {
                                                        DateUtils.isDateAfterCurrent(
                                                            it.deadlineDate,
                                                        )
                                                    }
                                                Resource.Success(activeAssignments)
                                            } else if (hasError) {
                                                Resource.Error(
                                                    errorMessage
                                                        ?: "Gagal memuat tugas untuk mata kuliah dosen.",
                                                )
                                            } else {
                                                Resource.Success(emptyList())
                                            }
                                        }
                                    }
                                }

                                is Resource.Error -> {
                                    flowOf(
                                        Resource.Error(
                                            allCoursesResource.message
                                                ?: "Gagal memuat daftar semua mata kuliah.",
                                        ),
                                    )
                                }

                                is Resource.Loading -> {
                                    flowOf(Resource.Loading())
                                }
                            }
                        }
                    }
                }.collectLatest {
                    _assignmentListLecturerState.value = it
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchTodaySchedule() {
        viewModelScope.launch {
            _todayScheduleState.value = Resource.Loading()
            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { lecturerId ->
                    if (lecturerId != null) {
                        virtualClassUseCase.getTodayScheduleLecturer(lecturerId)
                    } else {
                        flowOf(Resource.Error("ID Dosen pengguna tidak ditemukan atau tidak valid."))
                    }
                }.collectLatest {
                    _todayScheduleState.value = it
                }
        }
    }

    fun getCourseNameById(courseId: String): String = _courseLecturerMapState.value[courseId]?.first ?: "Course Tidak Dikenal"

    fun getCourseImageById(courseId: String): String? = _courseLecturerMapState.value[courseId]?.second
}
