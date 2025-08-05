package com.mjs.dosen.di

import com.mjs.dosen.presentation.MainActivityViewModel
import com.mjs.dosen.presentation.setting.SettingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dosenModule =
    module {
        viewModel { MainActivityViewModel(get()) }
        viewModel { SettingViewModel(get()) }
    }
