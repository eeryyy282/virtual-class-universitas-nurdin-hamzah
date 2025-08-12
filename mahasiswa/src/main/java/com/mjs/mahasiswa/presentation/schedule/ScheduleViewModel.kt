package com.mjs.mahasiswa.presentation.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
    private val appPreference: AppPreference,
) : ViewModel() {
    private val _schedule = MutableLiveData<Resource<List<Kelas>>>()
    val schedule: LiveData<Resource<List<Kelas>>> get() = _schedule

    fun getStudentSchedule() {
        viewModelScope.launch {
            val nim = appPreference.getLoggedInUserId().firstOrNull()
            val userType = appPreference.getLoggedInUserType().firstOrNull()

            if (nim != null && userType == AppPreference.USER_TYPE_MAHASISWA) {
                virtualClassUseCase.getAllSchedulesByNim(nim).collect {
                    _schedule.value = it
                }
            } else {
                _schedule.value = Resource.Error("User not logged in or not a Mahasiswa")
            }
        }
    }

    fun getDosenNameByNidn(nidn: Int): Flow<String> =
        virtualClassUseCase
            .getDosenByNidn(nidn)
            .filter { it !is Resource.Loading }
            .map { resource ->
                when (resource) {
                    is Resource.Success -> resource.data?.nama ?: nidn
                    is Resource.Error -> nidn
                    else -> nidn
                }.toString()
            }
}
