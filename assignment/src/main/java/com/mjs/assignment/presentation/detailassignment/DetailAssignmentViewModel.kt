package com.mjs.assignment.presentation.detailassignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Assignment
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class DetailAssignmentViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _assignmentDetail = MutableLiveData<Assignment?>()
    val assignmentDetail: LiveData<Assignment?> = _assignmentDetail

    private val _courseDetail = MutableLiveData<Resource<Course>>()
    val courseDetail: LiveData<Resource<Course>> = _courseDetail

    private val _userRole = MutableLiveData<String?>()
    val userRole: LiveData<String?> = _userRole

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> = _errorState

    private val _deleteAssignmentResult = MutableLiveData<Resource<String>>()
    val deleteAssignmentResult: LiveData<Resource<String>> = _deleteAssignmentResult

    init {
        viewModelScope.launch {
            virtualClassUseCase.getLoggedInUserType().collect {
                _userRole.value = it
            }
        }
    }

    fun loadAssignmentAndCourseDetailsById(assignmentId: Long) {
        viewModelScope.launch {
            virtualClassUseCase.getAssignmentById(assignmentId).collect { assignmentResource ->
                when (assignmentResource) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val assignmentData = assignmentResource.data
                        _assignmentDetail.value = assignmentData
                        if (assignmentData != null) {
                            virtualClassUseCase
                                .getCourseById(assignmentData.courseId)
                                .collect { courseResource ->
                                    _courseDetail.value = courseResource
                                }
                        } else {
                            _errorState.value = "Data tugas tidak ditemukan setelah refresh."
                        }
                    }

                    is Resource.Error -> {
                        _assignmentDetail.value = null
                        _errorState.value =
                            assignmentResource.message ?: "Gagal memuat detail tugas."
                    }
                }
            }
        }
    }

    fun deleteAssignment(assignmentId: Long) {
        viewModelScope.launch {
            virtualClassUseCase.deleteAssignmentById(assignmentId).collect { result ->
                _deleteAssignmentResult.value = result
            }
        }
    }

    fun clearErrorState() {
        _errorState.value = null
    }
}
