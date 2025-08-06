package com.mjs.mahasiswa.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase

class MainActivityViewModel(
    virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()
}
