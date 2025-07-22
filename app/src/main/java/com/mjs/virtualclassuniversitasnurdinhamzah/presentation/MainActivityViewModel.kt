package com.mjs.virtualclassuniversitasnurdinhamzah.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mjs.core.domain.usecase.pref.ThemeUseCase

class MainActivityViewModel(
    themeUseCase: ThemeUseCase,
) : ViewModel() {
    val getThemeSetting = themeUseCase.getThemeSetting().asLiveData()
}
