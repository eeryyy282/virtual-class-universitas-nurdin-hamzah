package com.mjs.virtualclassuniversitasnurdinhamzah.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mjs.core.data.source.local.pref.AppPreference
import kotlinx.coroutines.flow.Flow

class SplashScreenViewModel(
    private val appPreference: AppPreference,
) : ViewModel() {
    val getThemeSetting: LiveData<Boolean> = appPreference.getThemeSetting().asLiveData()

    fun getLoginStatus(): Flow<Boolean> = appPreference.getLoginStatus()

    fun getUserType(): Flow<String?> = appPreference.getLoggedInUserType()
}
