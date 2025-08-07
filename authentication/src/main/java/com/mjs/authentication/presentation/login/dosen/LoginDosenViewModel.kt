package com.mjs.authentication.presentation.login.dosen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.authentication.presentation.utils.LoginResult
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.data.source.local.room.dao.AuthDao
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class LoginDosenViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
    private val authDao: AuthDao,
    private val appPreference: AppPreference,
) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            virtualClassUseCase.saveThemeSetting(isDarkModeActive)
        }
    }

    fun login(
        nidn: String,
        password: String,
    ) {
        viewModelScope.launch {
            if (nidn.isBlank() || password.isBlank()) {
                _loginResult.value = LoginResult.Error("NIDN and password cannot be empty")
                return@launch
            }
            val dosen = authDao.loginDosen(nidn.toInt(), password)
            if (dosen != null) {
                appPreference.saveLoginSession(dosen.nidn, AppPreference.USER_TYPE_DOSEN)
                _loginResult.value = LoginResult.Success(AppPreference.USER_TYPE_DOSEN)
            } else {
                _loginResult.value = LoginResult.Error("Invalid NIDN or password")
            }
        }
    }
}
