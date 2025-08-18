package com.mjs.detailtask.presentation.submittask

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SubmitTaskViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _submissionStatus = MutableLiveData<Resource<String>>()
    val submissionStatus: LiveData<Resource<String>> = _submissionStatus

    fun submitTask(
        assignmentId: Int,
        notes: String,
        attachmentPath: String?,
    ) {
        viewModelScope.launch {
            _submissionStatus.value = Resource.Loading()
            try {
                val userId = virtualClassUseCase.getLoggedInUserId().first()
                val userType = virtualClassUseCase.getLoggedInUserType().first()

                if (userId != null && userType == VirtualClassUseCase.USER_TYPE_MAHASISWA) {
                    val nim = userId
                    val currentDate =
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                    val submissionEntity =
                        SubmissionEntity(
                            assignmentId = assignmentId,
                            nim = nim,
                            submissionDate = currentDate,
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
