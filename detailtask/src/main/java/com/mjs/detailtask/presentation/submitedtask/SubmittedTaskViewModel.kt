package com.mjs.detailtask.presentation.submitedtask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import com.mjs.core.ui.task.SubmissionListItem
import kotlinx.coroutines.launch

class SubmittedTaskViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _submittedTasks = MutableLiveData<Resource<List<SubmissionListItem>>>()
    val submittedTasks: LiveData<Resource<List<SubmissionListItem>>> = _submittedTasks

    fun getSubmissionsByAssignment(assignmentId: Int) {
        viewModelScope.launch {
            virtualClassUseCase.getSubmissionListItemsByAssignment(assignmentId).collect {
                _submittedTasks.value = it
            }
        }
    }
}
