package com.mjs.authentication.presentation.login.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.authentication.presentation.utils.LoginResult
import com.mjs.core.data.Resource
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class LoginStudentViewModel(
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
        studentId: String,
        password: String,
    ) {
        viewModelScope.launch {
            if (studentId.isBlank() || password.isBlank()) {
                _loginResult.value = LoginResult.Error("NIM dan Password tidak boleh kosong")
                return@launch
            }
            virtualClassUseCase.loginStudent(studentId.toLong(), password).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val student = resource.data
                        if (student != null) {
                            virtualClassUseCase.saveLoginSession(
                                student.studentId,
                                VirtualClassUseCase.USER_TYPE_STUDENT,
                            )
                            _loginResult.value =
                                LoginResult.Success(VirtualClassUseCase.USER_TYPE_STUDENT)
                        } else {
                            _loginResult.value = LoginResult.Error("NIM atau Password Invalid")
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
