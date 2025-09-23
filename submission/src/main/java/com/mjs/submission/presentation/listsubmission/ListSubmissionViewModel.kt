package com.mjs.submission.presentation.listsubmission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import com.mjs.core.ui.submission.SubmissionListItem
import kotlinx.coroutines.launch

class ListSubmissionViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _submissions = MutableLiveData<Resource<List<SubmissionListItem>>>()
    val submissions: LiveData<Resource<List<SubmissionListItem>>> = _submissions

    fun getSubmissionsByAssignment(assignmentId: Long) {
        viewModelScope.launch {
            virtualClassUseCase.getSubmissionListItemsByAssignment(assignmentId).collect {
                _submissions.value = it
            }
        }
    }
}
