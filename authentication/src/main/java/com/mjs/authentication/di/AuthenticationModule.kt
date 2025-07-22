package com.mjs.authentication.di

import com.mjs.authentication.presentation.login.dosen.LoginDosenViewModel
import com.mjs.authentication.presentation.login.mahasiswa.LoginMahasiswaViewModel
import com.mjs.authentication.presentation.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val loginDeosenModule =
    module {
        viewModel { LoginDosenViewModel(get()) }
    }

val loginMahasiswaModule =
    module {
        viewModel { LoginMahasiswaViewModel(get()) }
    }

val registerModule =
    module {
        viewModel { RegisterViewModel(get()) }
    }
