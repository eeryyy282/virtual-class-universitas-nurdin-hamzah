package com.mjs.course.presentation.registered

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.model.Lecturer
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class DetailCourseRegisteredViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _courseDetails = MutableLiveData<Resource<Course>>()
    val courseDetails: LiveData<Resource<Course>> = _courseDetails

    private val _lecturerDetail = MutableLiveData<Resource<Lecturer>>()
    val lecturerDetail: LiveData<Resource<Lecturer>> = _lecturerDetail

    private val _leaveCourseStatus =
        MutableLiveData<Resource<String>>()
    val leaveCourseStatus: LiveData<Resource<String>> =
        _leaveCourseStatus

    private val _loggedInUserId = MutableLiveData<Long?>()
    val loggedInUserId: LiveData<Long?> = _loggedInUserId

    private val _loggedInUserType = MutableLiveData<String?>()
    val loggedInUserType: LiveData<String?> = _loggedInUserType

    private val _pendingEnrollmentCount = MutableLiveData<Resource<Int>>()
    val pendingEnrollmentCount: LiveData<Resource<Int>> = _pendingEnrollmentCount

    init {
        fetchLoggedInUserDetails()
    }

    private fun fetchLoggedInUserDetails() {
        viewModelScope.launch {
            virtualClassUseCase.getLoggedInUserId().collect { userId ->
                _loggedInUserId.value = userId
            }
        }
        viewModelScope.launch {
            virtualClassUseCase.getLoggedInUserType().collect { userType ->
                _loggedInUserType.value = userType
            }
        }
    }

    fun fetchCourseDetails(courseId: String) {
        viewModelScope.launch {
            _courseDetails.value = Resource.Loading()
            virtualClassUseCase.getAllCourse().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val course =
                            resource.data?.find { it.courseId == courseId }
                        if (course != null) {
                            _courseDetails.value = Resource.Success(course)
                            fetchLecturerDetails(course.lecturerId)
                            if (_loggedInUserType.value == VirtualClassUseCase.USER_TYPE_LECTURER) {
                                fetchPendingEnrollmentCount(course.courseId)
                            }
                        } else {
                            _courseDetails.value =
                                Resource.Error("Course tidak ditemukan")
                        }
                    }

                    is Resource.Error -> {
                        _courseDetails.value =
                            Resource.Error(
                                resource.message ?: "Gagal memuat detail course",
                            )
                    }

                    is Resource.Loading -> {
                    }
                }
            }
        }
    }

    private fun fetchLecturerDetails(lecturerId: Long) {
        viewModelScope.launch {
            _lecturerDetail.value = Resource.Loading()
            virtualClassUseCase
                .getLecturerByLecturerId(lecturerId)
                .collect { resource ->
                    _lecturerDetail.value = resource
                }
        }
    }

    private fun fetchPendingEnrollmentCount(courseId: String) {
        viewModelScope.launch {
            virtualClassUseCase
                .getPendingEnrollmentRequestCount(courseId)
                .collect {
                    _pendingEnrollmentCount.value = it
                }
        }
    }

    fun leaveCourse(
        studentId: Long,
        courseId: String,
    ) {
        viewModelScope.launch {
            _leaveCourseStatus.value = Resource.Loading()
            virtualClassUseCase
                .leaveCourse(studentId, courseId)
                .collect {
                    _leaveCourseStatus.value = it
                }
        }
    }
}
