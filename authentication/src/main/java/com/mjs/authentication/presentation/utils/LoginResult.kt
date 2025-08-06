package com.mjs.authentication.presentation.utils

sealed class LoginResult {
    data class Success(
        val userType: String,
    ) : LoginResult()

    data class Error(
        val message: String,
    ) : LoginResult()
}
