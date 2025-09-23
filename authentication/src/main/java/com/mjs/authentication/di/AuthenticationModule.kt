package com.mjs.authentication.di

import com.mjs.authentication.presentation.login.lecturer.LoginLecturerViewModel
import com.mjs.authentication.presentation.login.student.LoginStudentViewModel
import com.mjs.authentication.presentation.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val loginLecturerModule =
    module {
        viewModel {
            LoginLecturerViewModel(
                virtualClassUseCase = get(),
            )
        }
    }

val loginStudentModule =
    module {
        viewModel {
            LoginStudentViewModel(
                virtualClassUseCase = get(),
            )
        }
    }

val registerModule =
    module {
        viewModel { RegisterViewModel(get()) }
    }
