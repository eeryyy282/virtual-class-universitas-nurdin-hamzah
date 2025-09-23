package com.mjs.student.presentation.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Assignment
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class AssignmentViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    private val _enrolledCoursesMapState =
        MutableStateFlow<Map<String, Pair<String, String?>>>(emptyMap())
    val enrolledCoursesMapState: StateFlow<Map<String, Pair<String, String?>>> =
        _enrolledCoursesMapState

    val assignments: StateFlow<Resource<Pair<List<Assignment>, List<Assignment>>>> =
        virtualClassUseCase
            .getLoggedInUserId()
            .flatMapLatest { studentId ->
                if (studentId == null) {
                    flowOf(Resource.Error("NIM pengguna tidak ditemukan."))
                } else {
                    loadEnrolledCoursesAndAllCourseDetails(studentId).flatMapLatest { enrolledCoursesDetailsResult ->
                        when (enrolledCoursesDetailsResult) {
                            is Resource.Loading -> flowOf(Resource.Loading())
                            is Resource.Error ->
                                flowOf(
                                    Resource.Error(
                                        enrolledCoursesDetailsResult.message
                                            ?: "Gagal memuat detail mata kuliah.",
                                    ),
                                )

                            is Resource.Success -> {
                                val approvedEnrolledCourseIds =
                                    enrolledCoursesDetailsResult.data?.first ?: emptyList()
                                val approvedAllCoursesMap =
                                    enrolledCoursesDetailsResult.data?.second ?: emptyMap()
                                _enrolledCoursesMapState.value = approvedAllCoursesMap

                                if (approvedEnrolledCourseIds.isEmpty()) {
                                    flowOf(
                                        Resource.Success(
                                            Pair(
                                                emptyList(),
                                                emptyList(),
                                            ),
                                        ),
                                    )
                                } else {
                                    val notFinishedAssignmentsFlows =
                                        approvedEnrolledCourseIds.map { courseId ->
                                            virtualClassUseCase.getNotFinishedAssignment(
                                                studentId,
                                                courseId,
                                            )
                                        }
                                    val lateAssignmentsFlows =
                                        approvedEnrolledCourseIds.map { courseId ->
                                            virtualClassUseCase.getLateAssignment(
                                                studentId,
                                                courseId,
                                            )
                                        }

                                    combine(notFinishedAssignmentsFlows) { results -> results.toList() }
                                        .combine(
                                            combine(lateAssignmentsFlows) { results -> results.toList() },
                                        ) { notFinishedResults, lateResults ->
                                            processAssignmentResults(
                                                notFinishedResults,
                                                lateResults,
                                            )
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

    private fun loadEnrolledCoursesAndAllCourseDetails(
        studentId: Long,
    ): Flow<Resource<Pair<List<String>, Map<String, Pair<String, String?>>>>> =
        virtualClassUseCase
            .getEnrolledCourse(studentId)
            .combine(virtualClassUseCase.getAllCourse()) { enrolledCoursesResource, allCoursesResource ->
                if (enrolledCoursesResource is Resource.Success && allCoursesResource is Resource.Success) {
                    val approvedEnrollments =
                        enrolledCoursesResource.data?.filter { it.status == "approved" }
                    val approvedEnrolledCourseIds =
                        approvedEnrollments?.map { it.courseId } ?: emptyList()
                    val allCoursesList = allCoursesResource.data
                    val approvedAllCoursesMap =
                        allCoursesList
                            ?.associate {
                                it.courseId to
                                    Pair(
                                        it.courseName,
                                        it.courseImage,
                                    )
                            }?.filterKeys { it in approvedEnrolledCourseIds }
                            ?: emptyMap()
                    Resource.Success(Pair(approvedEnrolledCourseIds, approvedAllCoursesMap))
                } else if (enrolledCoursesResource is Resource.Error) {
                    Resource.Error(
                        enrolledCoursesResource.message ?: "Gagal memuat kelas yang diikuti.",
                    )
                } else if (allCoursesResource is Resource.Error) {
                    Resource.Error(
                        allCoursesResource.message ?: "Gagal memuat semua data mata kuliah.",
                    )
                } else {
                    Resource.Loading()
                }
            }

    private fun processAssignmentResults(
        notFinishedResults: List<Resource<List<Assignment>>>,
        lateResults: List<Resource<List<Assignment>>>,
    ): Resource<Pair<List<Assignment>, List<Assignment>>> {
        val allNotFinishedAssignments = mutableListOf<Assignment>()
        val allLateAssignments = mutableListOf<Assignment>()
        var firstError: String? = null
        var isLoading = false

        notFinishedResults.forEach {
            when (it) {
                is Resource.Success -> allNotFinishedAssignments.addAll(it.data ?: emptyList())
                is Resource.Error -> if (firstError == null) firstError = it.message
                is Resource.Loading -> isLoading = true
            }
        }

        lateResults.forEach {
            when (it) {
                is Resource.Success -> allLateAssignments.addAll(it.data ?: emptyList())
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
                        allNotFinishedAssignments.distinctBy { it.assignmentId },
                        allLateAssignments.distinctBy { it.assignmentId },
                    ),
                )
        }
    }

    fun getCourseName(courseId: String): String = enrolledCoursesMapState.value[courseId]?.first ?: "Mata Kuliah Tidak Dikenal"

    fun getCourseImageUrl(courseId: String): String? = enrolledCoursesMapState.value[courseId]?.second
}
