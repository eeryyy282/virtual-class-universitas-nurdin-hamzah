package com.mjs.mahasiswa.di

import com.mjs.mahasiswa.presentation.MainActivityViewModel
import com.mjs.mahasiswa.presentation.home.HomeViewModel
import com.mjs.mahasiswa.presentation.setting.SettingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mahasiswaModule =
    module {
        viewModel { MainActivityViewModel(get()) }
        viewModel { SettingViewModel(get(), get(), get()) }
        viewModel { HomeViewModel(get(), get()) }
    }
