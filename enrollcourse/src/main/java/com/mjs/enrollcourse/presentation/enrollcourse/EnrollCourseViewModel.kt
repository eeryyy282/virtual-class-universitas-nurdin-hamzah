package com.mjs.enrollcourse.presentation.enrollcourse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EnrollCourseViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    @Suppress("ktlint:standard:backing-property-naming")
    private val _originalGroupedCourses =
        MutableLiveData<Resource<List<Pair<String, List<Course>>>>>()

    private val _groupedCourses =
        MutableLiveData<Resource<List<Pair<String, List<Course>>>>>()
    val groupedCourses: LiveData<Resource<List<Pair<String, List<Course>>>>> =
        _groupedCourses

    private val searchQuery = MutableLiveData<String>()

    private val _lecturerMap = MutableLiveData<Map<Long, String>>()
    val lecturerMap: LiveData<Map<Long, String>> = _lecturerMap

    init {
        fetchUnenrolledCoursesByStudentDepartment()
        searchQuery.observeForever { query ->
            filterCourses(query)
        }
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    private fun filterCourses(query: String?) {
        val originalResource =
            _originalGroupedCourses.value
        if (originalResource is Resource.Success) {
            val originalData = originalResource.data
            if (query.isNullOrBlank()) {
                _groupedCourses.value = originalResource
            } else {
                val filteredList =
                    originalData?.mapNotNull { (semester, courseList) ->
                        val filteredCourses =
                            courseList.filter { course ->
                                course.courseName.contains(query, ignoreCase = true) ||
                                    course.courseId.contains(query, ignoreCase = true) ||
                                    course.semester.contains(query, ignoreCase = true)
                            }
                        if (filteredCourses.isNotEmpty()) {
                            semester to filteredCourses
                        } else {
                            null
                        }
                    }
                if (filteredList.isNullOrEmpty()) {
                    _groupedCourses.value =
                        Resource.Success(emptyList())
                } else {
                    _groupedCourses.value =
                        Resource.Success(filteredList)
                }
            }
        } else {
            if (originalResource != null) {
                _groupedCourses.value = originalResource
            } else {
                _groupedCourses.value = Resource.Loading()
            }
        }
    }

    private fun fetchLecturerName(lecturerId: Long) {
        viewModelScope.launch {
            virtualClassUseCase.getLecturerByLecturerId(lecturerId).collect { resource ->
                if (resource is Resource.Success) {
                    resource.data?.let { lecturer ->
                        val currentMap =
                            _lecturerMap.value ?: emptyMap()
                        _lecturerMap.value =
                            currentMap + (lecturerId to lecturer.name)
                    }
                }
            }
        }
    }

    private fun fetchUnenrolledCoursesByStudentDepartment() {
        viewModelScope.launch {
            _originalGroupedCourses.value =
                Resource.Loading()
            _groupedCourses.value = Resource.Loading()
            try {
                val studentIdValue =
                    virtualClassUseCase.getLoggedInUserId().first()
                if (studentIdValue != null) {
                    val userType = virtualClassUseCase.getLoggedInUserType().first()
                    if (userType == VirtualClassUseCase.USER_TYPE_STUDENT) {
                        virtualClassUseCase
                            .getStudentByStudentId(studentIdValue)
                            .collect { studentResource ->
                                when (studentResource) {
                                    is Resource.Success -> {
                                        val student =
                                            studentResource.data
                                        if (student != null) {
                                            fetchAndFilterCourses(
                                                studentIdValue,
                                                student.department,
                                            )
                                        } else {
                                            val error =
                                                Resource.Error<List<Pair<String, List<Course>>>>("Data mahasiswa tidak ditemukan.")
                                            _originalGroupedCourses.value =
                                                error
                                            _groupedCourses.value =
                                                error
                                        }
                                    }

                                    is Resource.Error -> {
                                        val error =
                                            Resource.Error<List<Pair<String, List<Course>>>>(
                                                studentResource.message
                                                    ?: "Gagal mendapatkan data mahasiswa.",
                                            )
                                        _originalGroupedCourses.value =
                                            error
                                        _groupedCourses.value =
                                            error
                                    }

                                    is Resource.Loading -> {
                                    }
                                }
                            }
                    } else {
                        val error =
                            Resource.Error<List<Pair<String, List<Course>>>>("Pengguna bukan mahasiswa.")
                        _originalGroupedCourses.value =
                            error
                        _groupedCourses.value = error
                    }
                } else {
                    val error =
                        Resource.Error<List<Pair<String, List<Course>>>>("ID pengguna tidak ditemukan.")
                    _originalGroupedCourses.value =
                        error
                    _groupedCourses.value = error
                }
            } catch (e: Exception) {
                val error =
                    Resource.Error<List<Pair<String, List<Course>>>>(
                        e.message
                            ?: "Terjadi kesalahan saat mengambil data mata kuliah.",
                    )
                _originalGroupedCourses.value =
                    error
                _groupedCourses.value = error
            }
        }
    }

    private fun fetchAndFilterCourses(
        studentId: Long,
        department: String,
    ) {
        viewModelScope.launch {
            val allCoursesFlow =
                virtualClassUseCase.getAllCourseByDepartment(department)
            val enrolledCoursesFlow =
                virtualClassUseCase.getEnrolledCourse(studentId)

            allCoursesFlow
                .combine(enrolledCoursesFlow) { allCoursesResource, enrolledCoursesResource ->
                    Pair(
                        allCoursesResource,
                        enrolledCoursesResource,
                    )
                }.collect { (allCoursesResource, enrolledCoursesResource) ->
                    when {
                        allCoursesResource is Resource.Error -> {
                            val error =
                                Resource.Error<List<Pair<String, List<Course>>>>(
                                    allCoursesResource.message
                                        ?: "Gagal memuat semua mata kuliah.",
                                )
                            _originalGroupedCourses.value =
                                error
                            _groupedCourses.value = error
                        }

                        enrolledCoursesResource is Resource.Error -> {
                            val error =
                                Resource.Error<List<Pair<String, List<Course>>>>(
                                    enrolledCoursesResource.message
                                        ?: "Gagal memuat mata kuliah yang diikuti.",
                                )
                            _originalGroupedCourses.value =
                                error
                            _groupedCourses.value = error
                        }

                        allCoursesResource is Resource.Success && enrolledCoursesResource is Resource.Success -> {
                            val allCourses =
                                allCoursesResource.data
                            val enrolledCourses =
                                enrolledCoursesResource.data

                            if (allCourses != null && enrolledCourses != null) {
                                val enrolledApprovedCourseIds =
                                    enrolledCourses
                                        .filter { it.status == "approved" }
                                        .map { it.courseId }
                                        .toSet()
                                val displayCourses =
                                    allCourses.filter {
                                        it.courseId !in enrolledApprovedCourseIds
                                    }

                                val grouped =
                                    displayCourses
                                        .groupBy { it.semester }
                                        .toList()
                                        .sortedBy { it.first }
                                val successResult = Resource.Success(grouped)
                                _originalGroupedCourses.value =
                                    successResult
                                filterCourses(searchQuery.value)

                                val allLecturerIds =
                                    displayCourses
                                        .map { it.lecturerId }
                                        .distinct()
                                allLecturerIds.forEach { lecturerIdValue ->
                                    fetchLecturerName(lecturerIdValue)
                                }
                            } else {
                                val successEmpty =
                                    Resource.Success<List<Pair<String, List<Course>>>>(emptyList())
                                _originalGroupedCourses.value =
                                    successEmpty
                                _groupedCourses.value =
                                    successEmpty
                            }
                        }

                        allCoursesResource is Resource.Loading || enrolledCoursesResource is Resource.Loading -> {
                            _originalGroupedCourses.value =
                                Resource.Loading()
                            _groupedCourses.value =
                                Resource.Loading()
                        }
                    }
                }
        }
    }
}
