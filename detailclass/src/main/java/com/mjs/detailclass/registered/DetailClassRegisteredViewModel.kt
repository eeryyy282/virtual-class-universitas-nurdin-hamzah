package com.mjs.detailclass.registered

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase

class DetailClassRegisteredViewModel(
    virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()
}
