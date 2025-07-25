package com.mjs.mahasiswa.di

import com.mjs.mahasiswa.presentation.MainActivityViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mahasiswaModule =
    module {
        viewModel { MainActivityViewModel(get()) }
    }
