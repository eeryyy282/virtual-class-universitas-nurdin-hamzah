package com.mjs.lecturer.presentation.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Assignment
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    @Suppress("ktlint:standard:backing-property-naming")
    private val _allCoursesMap = MutableStateFlow<Map<String, Pair<String, String?>>>(emptyMap())

    val assignments: StateFlow<Resource<Pair<List<Assignment>, List<Assignment>>>> =
        virtualClassUseCase
            .getLoggedInUserId()
            .flatMapLatest { lecturerIdResource ->
                val lecturerId =
                    lecturerIdResource
                if (lecturerId == null) {
                    flowOf(Resource.Error("ID Dosen tidak ditemukan."))
                } else {
                    virtualClassUseCase.getAllCourse().flatMapLatest { allCoursesResource ->
                        when (allCoursesResource) {
                            is Resource.Loading -> flowOf(Resource.Loading())
                            is Resource.Error ->
                                flowOf(
                                    Resource.Error(
                                        allCoursesResource.message
                                            ?: "Gagal memuat detail semua mata kuliah.",
                                    ),
                                )

                            is Resource.Success -> {
                                _allCoursesMap.value = allCoursesResource.data?.associate {
                                    it.courseId to Pair(it.courseName, it.courseImage)
                                } ?: emptyMap()

                                val activeAssignmentsFlow =
                                    virtualClassUseCase.getActiveAssignmentsForLecturer(lecturerId)
                                val pastDeadlineAssignmentsFlow =
                                    virtualClassUseCase.getPastDeadlineAssignmentsForLecturer(
                                        lecturerId,
                                    )

                                combine(
                                    activeAssignmentsFlow,
                                    pastDeadlineAssignmentsFlow,
                                ) { activeResult, pastResult ->
                                    processAssignmentResults(activeResult, pastResult)
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

    private fun processAssignmentResults(
        activeResult: Resource<List<Assignment>>,
        pastResult: Resource<List<Assignment>>,
    ): Resource<Pair<List<Assignment>, List<Assignment>>> {
        val activeAssignments = mutableListOf<Assignment>()
        val pastDeadlineAssignments = mutableListOf<Assignment>()
        var firstError: String? = null
        var isLoading = false

        when (activeResult) {
            is Resource.Success -> activeAssignments.addAll(activeResult.data ?: emptyList())
            is Resource.Error -> firstError = activeResult.message
            is Resource.Loading -> isLoading = true
        }

        when (pastResult) {
            is Resource.Success -> pastDeadlineAssignments.addAll(pastResult.data ?: emptyList())
            is Resource.Error -> if (firstError == null) firstError = pastResult.message
            is Resource.Loading -> isLoading = true
        }

        return when {
            firstError != null -> Resource.Error(firstError)
            isLoading -> Resource.Loading()
            else -> Resource.Success(Pair(activeAssignments, pastDeadlineAssignments))
        }
    }

    fun getCourseNameById(courseId: String): String = _allCoursesMap.value[courseId]?.first ?: "Mata kuliah Tidak Dikenal"

    fun getCoursePhotoProfileById(courseId: String): String? = _allCoursesMap.value[courseId]?.second
}
