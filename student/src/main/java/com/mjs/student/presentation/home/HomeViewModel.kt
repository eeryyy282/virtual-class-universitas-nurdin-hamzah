package com.mjs.student.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.domain.model.Assignment
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.model.Student
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

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    private val _studentProfileState = MutableStateFlow<Resource<Student>?>(null)
    val studentProfileState: StateFlow<Resource<Student>?> = _studentProfileState

    private val _assignmentListState = MutableStateFlow<Resource<List<Assignment>>?>(null)
    val assignmentListState: StateFlow<Resource<List<Assignment>>?> = _assignmentListState

    private val _enrolledCoursesMapState =
        MutableStateFlow<Map<String, Pair<String, String?>>>(emptyMap())
    val enrolledCoursesMapState: StateFlow<Map<String, Pair<String, String?>>> =
        _enrolledCoursesMapState

    private val _attendanceStreakState = MutableStateFlow<Resource<Int>?>(null)
    val attendanceStreakState: StateFlow<Resource<Int>?> = _attendanceStreakState

    private val _todayScheduleState = MutableStateFlow<Resource<List<Course>>?>(null)
    val todayScheduleState: StateFlow<Resource<List<Course>>?> = _todayScheduleState

    init {
        loadStudentProfile()
        loadAssignmentsAndEnrolledCourses()
        loadAttendanceStreak()
        loadTodaySchedule()
    }

    private fun loadStudentProfile() {
        viewModelScope.launch {
            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { studentId ->
                    if (studentId != null) {
                        virtualClassUseCase.getStudentByStudentId(studentId)
                    } else {
                        flowOf(Resource.Error("NIM tidak ditemukan"))
                    }
                }.collectLatest {
                    _studentProfileState.value = it
                }
        }
    }

    private fun loadAttendanceStreak() {
        viewModelScope.launch {
            _attendanceStreakState.value = Resource.Loading()
            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { studentId ->
                    if (studentId == null) {
                        flowOf(Resource.Error("NIM pengguna tidak ditemukan."))
                    } else {
                        virtualClassUseCase
                            .getEnrolledCourse(studentId)
                            .flatMapLatest { enrolledCoursesResource ->
                                when (enrolledCoursesResource) {
                                    is Resource.Success -> {
                                        val enrollments = enrolledCoursesResource.data
                                        if (!enrollments.isNullOrEmpty()) {
                                            val approvedEnrollments =
                                                enrollments.filter { it.status == "approved" }
                                            if (approvedEnrollments.isNotEmpty()) {
                                                val firstCourseId = approvedEnrollments[0].courseId
                                                virtualClassUseCase.getAttendanceStreak(
                                                    studentId,
                                                    firstCourseId,
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
                                                enrolledCoursesResource.message
                                                    ?: "Gagal memuat kelas yang diikuti untuk mengambil data streak.",
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

    private fun loadAssignmentsAndEnrolledCourses() {
        viewModelScope.launch {
            _assignmentListState.value = Resource.Loading()

            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { loggedInStudentId ->
                    if (loggedInStudentId == null) {
                        flowOf(Resource.Error("NIM pengguna tidak ditemukan."))
                    } else {
                        val enrolledCoursesFlow: Flow<Resource<List<EnrollmentEntity>>> =
                            virtualClassUseCase.getEnrolledCourse(loggedInStudentId)
                        val allCoursesFlow: Flow<Resource<List<Course>>> =
                            virtualClassUseCase.getAllCourse()

                        combine(
                            enrolledCoursesFlow,
                            allCoursesFlow,
                        ) { enrolledCoursesResource, allCoursesResource ->
                            Pair(
                                enrolledCoursesResource,
                                allCoursesResource,
                            )
                        }.flatMapLatest { (enrolledCoursesResource, allCoursesResource) ->
                            if (enrolledCoursesResource is Resource.Success && allCoursesResource is Resource.Success) {
                                val enrollments = enrolledCoursesResource.data
                                val allCoursesList = allCoursesResource.data

                                if (enrollments.isNullOrEmpty()) {
                                    _enrolledCoursesMapState.value = emptyMap()
                                    flowOf(Resource.Success(emptyList()))
                                } else if (allCoursesList.isNullOrEmpty()) {
                                    _enrolledCoursesMapState.value = emptyMap()
                                    flowOf(
                                        Resource.Error("Gagal memuat detail mata kuliah untuk kelas yang diikuti."),
                                    )
                                } else {
                                    val approvedEnrollments =
                                        enrollments.filter { it.status == "approved" }
                                    val allCoursesMap = allCoursesList.associateBy { it.courseId }
                                    _enrolledCoursesMapState.value =
                                        approvedEnrollments
                                            .mapNotNull { enrollment ->
                                                allCoursesMap[enrollment.courseId]?.let {
                                                    enrollment.courseId to
                                                        Pair(
                                                            it.courseName,
                                                            it.courseImage,
                                                        )
                                                }
                                            }.toMap()

                                    if (approvedEnrollments.isEmpty()) {
                                        flowOf(Resource.Success(emptyList()))
                                    } else {
                                        val assignmentFlows: List<Flow<Resource<List<Assignment>>>> =
                                            approvedEnrollments.map { enrollment ->
                                                virtualClassUseCase.getNotFinishedAssignment(
                                                    loggedInStudentId,
                                                    enrollment.courseId,
                                                )
                                            }

                                        combine(assignmentFlows) { results: Array<Resource<List<Assignment>>> ->
                                            val allAssignments = mutableListOf<Assignment>()
                                            var firstError: String? = null
                                            var isLoadingCombined = false

                                            results.forEach { resource ->
                                                when (resource) {
                                                    is Resource.Success ->
                                                        allAssignments.addAll(
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

                                                isLoadingCombined && allAssignments.isEmpty() -> Resource.Loading()
                                                else -> Resource.Success(allAssignments.distinctBy { it.assignmentId })
                                            }
                                        }
                                    }
                                }
                            } else if (enrolledCoursesResource is Resource.Error) {
                                flowOf(
                                    Resource.Error(
                                        enrolledCoursesResource.message
                                            ?: "Gagal memuat kelas yang diikuti.",
                                    ),
                                )
                            } else if (allCoursesResource is Resource.Error) {
                                flowOf(
                                    Resource.Error(
                                        allCoursesResource.message
                                            ?: "Gagal memuat semua data mata kuliah.",
                                    ),
                                )
                            } else {
                                flowOf(Resource.Loading())
                            }
                        }
                    }
                }.collectLatest { resource: Resource<List<Assignment>> ->
                    _assignmentListState.value = resource
                }
        }
    }

    private fun loadTodaySchedule() {
        viewModelScope.launch {
            _todayScheduleState.value = Resource.Loading()
            virtualClassUseCase
                .getLoggedInUserId()
                .flatMapLatest { studentId ->
                    if (studentId == null) {
                        flowOf(Resource.Error("NIM pengguna tidak ditemukan."))
                    } else {
                        virtualClassUseCase.getTodaySchedule(studentId)
                    }
                }.collectLatest {
                    _todayScheduleState.value = it
                }
        }
    }

    fun getCourseNameById(courseId: String): String = enrolledCoursesMapState.value[courseId]?.first ?: "Mata Kuliah Tidak Dikenal"

    fun getCoursePhotoProfileById(courseId: String): String? = enrolledCoursesMapState.value[courseId]?.second
}
