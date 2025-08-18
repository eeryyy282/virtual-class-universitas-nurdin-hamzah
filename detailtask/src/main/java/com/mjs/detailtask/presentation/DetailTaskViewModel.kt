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

    private val _tugasDetail = MutableLiveData<Tugas>()
    val tugasDetail: LiveData<Tugas> = _tugasDetail

    private val _kelasDetail = MutableLiveData<Resource<Kelas>>()
    val kelasDetail: LiveData<Resource<Kelas>> = _kelasDetail

    private val _userRole = MutableLiveData<String?>()
    val userRole: LiveData<String?> = _userRole

    init {
        viewModelScope.launch {
            virtualClassUseCase.getLoggedInUserType().collect {
                _userRole.value = it
            }
        }
    }

    fun loadTaskDetails(tugas: Tugas) {
        _tugasDetail.value = tugas
        viewModelScope.launch {
            virtualClassUseCase.getKelasById(tugas.kelasId).collect { resource ->
                _kelasDetail.value = resource
            }
        }
    }
}
