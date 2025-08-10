package com.mjs.dosen.di

import com.mjs.dosen.presentation.MainActivityViewModel
import com.mjs.dosen.presentation.home.HomeViewModel
import com.mjs.dosen.presentation.schedule.ScheduleViewModel
import com.mjs.dosen.presentation.setting.SettingViewModel
import com.mjs.dosen.presentation.task.TaskViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dosenModule =
    module {
        viewModel { MainActivityViewModel(get()) }
        viewModel { SettingViewModel(get(), get(), get()) }
        viewModel { HomeViewModel(get(), get()) }
        viewModel { ScheduleViewModel(get(), get()) }
        viewModel { TaskViewModel(get(), get()) }
    }
