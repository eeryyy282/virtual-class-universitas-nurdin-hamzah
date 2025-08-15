package com.mjs.authentication.presentation.login.mahasiswa

// Import AppPreference akan dihapus karena konstanta diambil dari UseCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.authentication.presentation.utils.LoginResult
import com.mjs.core.data.Resource
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.launch

class LoginMahasiswaViewModel(
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
        nim: String,
        password: String,
    ) {
        viewModelScope.launch {
            if (nim.isBlank() || password.isBlank()) {
                _loginResult.value = LoginResult.Error("NIM dan Password tidak boleh kosong")
                return@launch
            }
            virtualClassUseCase.loginMahasiswa(nim, password).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val mahasiswa = resource.data
                        if (mahasiswa != null) {
                            virtualClassUseCase.saveLoginSession(
                                mahasiswa.nim,
                                VirtualClassUseCase.USER_TYPE_MAHASISWA,
                            )
                            _loginResult.value =
                                LoginResult.Success(VirtualClassUseCase.USER_TYPE_MAHASISWA)
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
