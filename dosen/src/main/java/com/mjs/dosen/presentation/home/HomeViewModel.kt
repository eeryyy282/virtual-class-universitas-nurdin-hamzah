package com.mjs.dosen.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Dosen
import com.mjs.core.domain.repository.IVirtualClassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(
    private val virtualClassRepository: IVirtualClassRepository,
    private val appPreference: AppPreference,
) : ViewModel() {
    private val _dosenData = MutableStateFlow<Resource<Dosen>?>(null)
    val dosenData: StateFlow<Resource<Dosen>?> = _dosenData

    init {
        fetchDosenData()
    }

    private fun fetchDosenData() {
        viewModelScope.launch {
            val nidn =
                appPreference
                    .getLoggedInUserId()
                    .firstOrNull() // Assuming NIDN is stored as loggedInUserId for dosen
            if (nidn != null &&
                appPreference
                    .getLoggedInUserType()
                    .firstOrNull() == AppPreference.USER_TYPE_DOSEN
            ) {
                virtualClassRepository.getDosenByNidn(nidn).collect {
                    _dosenData.value = it
                }
            }
        }
    }
}
