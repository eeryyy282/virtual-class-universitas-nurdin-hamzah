package com.mjs.mahasiswa.presentation.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    private val _schedule = MutableLiveData<Resource<List<Kelas>>>()
    val schedule: LiveData<Resource<List<Kelas>>> get() = _schedule

    fun getStudentSchedule() {
        viewModelScope.launch {
            val nim = virtualClassUseCase.getLoggedInUserId().firstOrNull()
            val userType = virtualClassUseCase.getLoggedInUserType().firstOrNull()

            if (nim != null && userType == VirtualClassUseCase.USER_TYPE_MAHASISWA) {
                virtualClassUseCase.getAllSchedulesByNim(nim).collect {
                    _schedule.value = it
                }
            } else {
                _schedule.value = Resource.Error("User tidak melakukan login atau bukan mahasiswa")
            }
        }
    }

    fun getDosenNameByNidn(nidn: Int): Flow<String> =
        virtualClassUseCase
            .getDosenByNidn(nidn)
            .filter { it !is Resource.Loading }
            .map { resource ->
                when (resource) {
                    is Resource.Success ->
                        resource.data?.nama
                            ?: nidn.toString()

                    is Resource.Error -> nidn.toString()
                    else -> nidn.toString()
                }
            }
}
