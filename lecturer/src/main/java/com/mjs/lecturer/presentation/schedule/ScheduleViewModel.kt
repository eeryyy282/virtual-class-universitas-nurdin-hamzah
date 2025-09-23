package com.mjs.lecturer.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val schedule: StateFlow<Resource<List<Course>>> =
        virtualClassUseCase
            .getLoggedInUserId()
            .combine(virtualClassUseCase.getLoggedInUserType()) { lecturerId, userType ->
                Pair(lecturerId, userType)
            }.flatMapLatest { (lecturerId, userType) ->
                if (lecturerId != null && userType == VirtualClassUseCase.USER_TYPE_LECTURER) {
                    virtualClassUseCase.getAllSchedulesByLecturerId(lecturerId)
                } else if (lecturerId == null) {
                    flowOf(Resource.Error("Pengguna tidak Login atau ID Dosen tidak ditemukan."))
                } else {
                    flowOf(Resource.Error("Pengguna bukan Lecturer."))
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )
}
