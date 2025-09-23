package com.mjs.assignment.presentation.editassignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Assignment
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class EditAssignmentViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _updateAssignmentResult = MutableLiveData<Resource<String>>()
    val updateAssignmentResult: LiveData<Resource<String>> = _updateAssignmentResult

    fun updateAssignment(assignment: Assignment) {
        viewModelScope.launch {
            virtualClassUseCase.updateAssignment(assignment).collect {
                _updateAssignmentResult.value = it
            }
        }
    }
}
