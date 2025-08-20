package com.mjs.detailtask.presentation.edittask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Tugas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class EditTaskViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _updateTaskResult = MutableLiveData<Resource<String>>()
    val updateTaskResult: LiveData<Resource<String>> = _updateTaskResult

    fun updateTask(tugas: Tugas) {
        viewModelScope.launch {
            virtualClassUseCase.updateTask(tugas).collect {
                _updateTaskResult.value = it
            }
        }
    }
}
