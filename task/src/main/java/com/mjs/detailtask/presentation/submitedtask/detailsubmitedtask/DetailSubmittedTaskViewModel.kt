package com.mjs.detailtask.presentation.submitedtask.detailsubmitedtask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import com.mjs.core.ui.task.SubmissionListItem
import kotlinx.coroutines.launch

class DetailSubmittedTaskViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _submissionDetail = MutableLiveData<Resource<SubmissionListItem?>>()
    val submissionDetail: LiveData<Resource<SubmissionListItem?>> = _submissionDetail

    private val _updateResult = MutableLiveData<Resource<String>>()
    val updateResult: LiveData<Resource<String>> = _updateResult

    private var initialGrade: Int? = null
    private var initialNote: String? = null

    private val _hasUnsavedChanges = MutableLiveData<Boolean>(false)
    val hasUnsavedChanges: LiveData<Boolean> = _hasUnsavedChanges

    fun getSubmissionDetail(submissionId: Int) {
        viewModelScope.launch {
            virtualClassUseCase.getSubmissionDetailById(submissionId).collect { resource ->
                if (resource is Resource.Success) {
                    resource.data?.submissionEntity?.let {
                        setInitialSubmissionData(it.grade, it.note)
                    }
                }
                _submissionDetail.value = resource
            }
        }
    }

    fun setInitialSubmissionData(
        grade: Int?,
        note: String?,
    ) {
        initialGrade = grade
        initialNote = note ?: ""
        _hasUnsavedChanges.value = false
    }

    fun checkIfUnsavedChangesExist(
        currentGradeString: String?,
        currentNote: String?,
    ) {
        val currentGrade = currentGradeString?.toIntOrNull()
        val cNote = currentNote ?: ""

        val gradeChanged = currentGrade != initialGrade
        val noteChanged = cNote != (initialNote ?: "")

        _hasUnsavedChanges.value = gradeChanged || noteChanged
    }

    fun updateSubmissionGradeAndNote(
        submissionId: Int,
        grade: Int?,
        note: String?,
    ) {
        viewModelScope.launch {
            virtualClassUseCase
                .updateSubmissionGradeAndNote(submissionId, grade, note)
                .collect { resource ->
                    if (resource is Resource.Success) {
                        setInitialSubmissionData(grade, note)
                    }
                    _updateResult.value = resource
                }
        }
    }

    fun discardChanges() {
        _hasUnsavedChanges.value = false
    }
}
