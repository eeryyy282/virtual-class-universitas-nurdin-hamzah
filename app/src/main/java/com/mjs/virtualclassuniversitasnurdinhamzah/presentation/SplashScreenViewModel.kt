package com.mjs.virtualclassuniversitasnurdinhamzah.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase // Menggunakan UseCase
import kotlinx.coroutines.flow.Flow

class SplashScreenViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting: LiveData<Boolean> = virtualClassUseCase.getThemeSetting().asLiveData()

    fun getLoginStatus(): Flow<Boolean> = virtualClassUseCase.getLoginStatus()

    fun getUserType(): Flow<String?> = virtualClassUseCase.getLoggedInUserType()
}
