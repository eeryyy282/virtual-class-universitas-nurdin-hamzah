package com.mjs.enrollclass.presentation.enrollrequest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class EnrollRequestViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _enrollmentRequests = MutableLiveData<Resource<List<Mahasiswa>>>()
    val enrollmentRequests: LiveData<Resource<List<Mahasiswa>>> = _enrollmentRequests

    fun fetchEnrollmentRequests(kelasId: String) {
        viewModelScope.launch {
            virtualClassUseCase.getPendingEnrollmentRequests(kelasId).collect {
                _enrollmentRequests.value = it
            }
        }
    }
}
