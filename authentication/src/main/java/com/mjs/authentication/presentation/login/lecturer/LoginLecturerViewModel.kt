package com.mjs.authentication.presentation.login.lecturer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.authentication.presentation.utils.LoginResult
import com.mjs.core.data.Resource
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class LoginLecturerViewModel(
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
        lecturerId: Long,
        password: String,
    ) {
        viewModelScope.launch {
            if (lecturerId.toString().isBlank() || password.isBlank()) {
                _loginResult.value =
                    LoginResult.Error("NIDN atau NIP dan password tidak boleh kosong")
                return@launch
            }
            virtualClassUseCase.loginLecturer(lecturerId, password).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val lecturer = resource.data
                        if (lecturer != null) {
                            virtualClassUseCase.saveLoginSession(
                                lecturer.lecturerId,
                                VirtualClassUseCase.USER_TYPE_LECTURER,
                            )
                            _loginResult.value =
                                LoginResult.Success(VirtualClassUseCase.USER_TYPE_LECTURER)
                        } else {
                            _loginResult.value =
                                LoginResult.Error("NIDN atau NIP dan Password salah")
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
