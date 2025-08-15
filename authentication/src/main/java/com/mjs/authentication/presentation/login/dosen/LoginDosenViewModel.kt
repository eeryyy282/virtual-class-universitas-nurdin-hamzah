package com.mjs.authentication.presentation.login.dosen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.authentication.presentation.utils.LoginResult
import com.mjs.core.data.Resource
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class LoginDosenViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
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
                _loginResult.value = LoginResult.Error("NIDN dan password tidak boleh kosong")
                return@launch
            }
            virtualClassUseCase.loginDosen(nidn, password).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val dosen = resource.data
                        if (dosen != null) {
                            virtualClassUseCase.saveLoginSession(
                                dosen.nidn,
                                VirtualClassUseCase.USER_TYPE_DOSEN,
                            )
                            _loginResult.value =
                                LoginResult.Success(VirtualClassUseCase.USER_TYPE_DOSEN)
                        } else {
                            _loginResult.value = LoginResult.Error("NIDN dan Password salah")
                        }
                    }

                    is Resource.Error -> {
                        _loginResult.value =
                            LoginResult.Error(resource.message ?: "Terjadi Error")
                    }
                }
            }
        }
    }
}
