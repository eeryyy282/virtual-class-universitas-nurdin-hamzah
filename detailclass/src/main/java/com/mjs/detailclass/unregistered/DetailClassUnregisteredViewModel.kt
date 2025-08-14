package com.mjs.detailclass.unregistered

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase

class DetailClassUnregisteredViewModel(
    virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()
}
