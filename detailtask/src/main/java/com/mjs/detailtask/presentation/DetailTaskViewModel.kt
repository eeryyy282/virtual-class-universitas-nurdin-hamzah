package com.mjs.detailtask.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.model.Tugas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class DetailTaskViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _tugasDetail = MutableLiveData<Tugas?>()
    val tugasDetail: LiveData<Tugas?> = _tugasDetail

    private val _kelasDetail = MutableLiveData<Resource<Kelas>>()
    val kelasDetail: LiveData<Resource<Kelas>> = _kelasDetail

    private val _userRole = MutableLiveData<String?>()
    val userRole: LiveData<String?> = _userRole

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> = _errorState

    private val _deleteTaskResult = MutableLiveData<Resource<String>>()
    val deleteTaskResult: LiveData<Resource<String>> = _deleteTaskResult

    init {
        viewModelScope.launch {
            virtualClassUseCase.getLoggedInUserType().collect {
                _userRole.value = it
            }
        }
    }

    fun loadTaskAndClassDetailsById(assignmentId: Int) {
        viewModelScope.launch {
            virtualClassUseCase.getAssignmentById(assignmentId).collect { tugasResource ->
                when (tugasResource) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val taskData = tugasResource.data
                        _tugasDetail.value = taskData
                        if (taskData != null) {
                            virtualClassUseCase
                                .getKelasById(taskData.kelasId)
                                .collect { kelasResource ->
                                    _kelasDetail.value = kelasResource
                                }
                        } else {
                            _errorState.value = "Data tugas tidak ditemukan setelah refresh."
                        }
                    }

                    is Resource.Error -> {
                        _tugasDetail.value = null
                        _errorState.value = tugasResource.message ?: "Gagal memuat detail tugas."
                    }
                }
            }
        }
    }

    fun deleteTask(assignmentId: Int) {
        viewModelScope.launch {
            virtualClassUseCase.deleteAssignmentById(assignmentId).collect { result ->
                _deleteTaskResult.value = result
            }
        }
    }

    fun clearErrorState() {
        _errorState.value = null
    }
}
