package com.mjs.dosen.presentation.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
    private val appPreference: AppPreference,
) : ViewModel() {
    private val _schedule = MutableLiveData<Resource<List<Kelas>>>()
    val schedule: LiveData<Resource<List<Kelas>>> get() = _schedule

    fun getDosenSchedule() {
        viewModelScope.launch {
            val nidn = appPreference.getLoggedInUserId().firstOrNull()
            val userType = appPreference.getLoggedInUserType().firstOrNull()

            if (nidn != null && userType == AppPreference.USER_TYPE_DOSEN) {
                try {
                    _schedule.value = Resource.Loading()
                    virtualClassUseCase.getAllSchedulesByNidn(nidn).collect {
                        _schedule.value = it
                    }
                } catch (e: NumberFormatException) {
                    _schedule.value = Resource.Error("Invalid NIDN format")
                }
            } else {
                _schedule.value = Resource.Error("User not logged in or not a Dosen")
            }
        }
    }
}
