package com.mjs.dosen.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Kelas
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
    val schedule: StateFlow<Resource<List<Kelas>>> =
        virtualClassUseCase
            .getLoggedInUserId()
            .combine(virtualClassUseCase.getLoggedInUserType()) { nidn, userType ->
                Pair(nidn, userType)
            }.flatMapLatest { (nidn, userType) ->
                if (nidn != null && userType == VirtualClassUseCase.USER_TYPE_DOSEN) {
                    virtualClassUseCase.getAllSchedulesByNidn(nidn)
                } else if (nidn == null) {
                    flowOf(Resource.Error("Pengguna tidak Login atau NIDN tidak ditemukan."))
                } else {
                    flowOf(Resource.Error("Pengguna bukan Dosen."))
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )
}
