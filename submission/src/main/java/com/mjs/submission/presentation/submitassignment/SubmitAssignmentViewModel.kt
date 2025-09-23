package com.mjs.submission.presentation.submitassignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class SubmitAssignmentViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _submissionStatus = MutableLiveData<Resource<String>>()
    val submissionStatus: LiveData<Resource<String>> = _submissionStatus

    fun submitTask(
        assignmentId: Long,
        notes: String,
        attachmentPath: String?,
    ) {
        viewModelScope.launch {
            _submissionStatus.value = Resource.Loading()
            try {
                val userId = virtualClassUseCase.getLoggedInUserId().first()
                val userType = virtualClassUseCase.getLoggedInUserType().first()

                if (userId != null && userType == VirtualClassUseCase.USER_TYPE_STUDENT) {
                    val studentId = userId

                    val submissionEntity =
                        SubmissionEntity(
                            assignmentId = assignmentId,
                            studentId = studentId,
                            submissionDate = Date(),
                            attachment = attachmentPath,
                            note = notes,
                            grade = null,
                        )

                    virtualClassUseCase.insertSubmission(submissionEntity).collect {
                        _submissionStatus.value = it
                    }
                } else {
                    _submissionStatus.value =
                        Resource.Error("User tidak valid atau bukan mahasiswa.")
                }
            } catch (e: Exception) {
                _submissionStatus.value =
                    Resource.Error(e.message ?: "Terjadi kesalahan saat submit tugas")
            }
        }
    }
}
