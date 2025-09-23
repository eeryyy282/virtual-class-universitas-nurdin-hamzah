package com.mjs.course.presentation.unregistered

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.model.Lecturer
import com.mjs.core.domain.model.Student
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class DetailCourseUnregisteredViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _courseDetails = MutableLiveData<Resource<Course>>()
    val courseDetails: LiveData<Resource<Course>> = _courseDetails

    private val _lecturerDetails = MutableLiveData<Resource<Lecturer>>()
    val lecturerDetails: LiveData<Resource<Lecturer>> = _lecturerDetails

    private val _studentList = MutableLiveData<Resource<List<Student>>>()
    val studentList: LiveData<Resource<List<Student>>> = _studentList

    private val _studentCount = MutableLiveData<Resource<Int>>()
    val studentCount: LiveData<Resource<Int>> = _studentCount

    private val _enrollmentRequestStatus = MutableLiveData<Resource<String>>()
    val enrollmentRequestStatus: LiveData<Resource<String>> = _enrollmentRequestStatus

    private val _currentEnrollmentState = MutableLiveData<EnrollmentEntity?>()
    val currentEnrollmentState: LiveData<EnrollmentEntity?> = _currentEnrollmentState

    fun fetchCourseDetailsAndEnrollmentStatus(courseId: String) {
        viewModelScope.launch {
            val studentIdValue = virtualClassUseCase.getLoggedInUserId().first()
            if (studentIdValue != null) {
                virtualClassUseCase.getEnrolledCourse(studentIdValue).collect { resource ->
                    if (resource is Resource.Success) {
                        val currentEnrollment = resource.data?.find { it.courseId == courseId }
                        _currentEnrollmentState.value = currentEnrollment
                    }
                }
            }
        }

        viewModelScope.launch {
            virtualClassUseCase.getCourseById(courseId).collect {
                _courseDetails.value = it
                if (it is Resource.Success) {
                    it.data?.lecturerId?.let { lecturerIdValue ->
                        fetchLecturerDetails(lecturerIdValue)
                    }
                    fetchStudentsByCourseId(courseId)
                    fetchStudentCountByCourseId(courseId)
                }
            }
        }
    }

    private fun fetchLecturerDetails(lecturerId: Long) {
        viewModelScope.launch {
            virtualClassUseCase.getLecturerByLecturerId(lecturerId).collect {
                _lecturerDetails.value = it
            }
        }
    }

    private fun fetchStudentsByCourseId(courseId: String) {
        viewModelScope.launch {
            virtualClassUseCase
                .getStudentByCourseId(courseId)
                .collect {
                    _studentList.value = it
                }
        }
    }

    private fun fetchStudentCountByCourseId(courseId: String) {
        viewModelScope.launch {
            virtualClassUseCase
                .getStudentCountByCourseId(courseId)
                .collect {
                    _studentCount.value = it
                }
        }
    }

    fun enrollToCourse(courseId: String) {
        viewModelScope.launch {
            try {
                _enrollmentRequestStatus.value = Resource.Loading()
                val studentIdValue = virtualClassUseCase.getLoggedInUserId().first()
                if (studentIdValue == null) {
                    _enrollmentRequestStatus.value = Resource.Error("User tidak login")
                    return@launch
                }
                val enrollment =
                    EnrollmentEntity(
                        studentId = studentIdValue,
                        courseId = courseId,
                        enrollmentDate = Date(),
                        status = "pending",
                    )
                virtualClassUseCase.enrollToCourse(enrollment).collect {
                    _enrollmentRequestStatus.value = it
                    if (it is Resource.Success) {
                        _currentEnrollmentState.value = enrollment
                    }
                }
            } catch (e: Exception) {
                _enrollmentRequestStatus.value =
                    Resource.Error(e.message ?: "Gagal mendaftar ke course")
            }
        }
    }
}
