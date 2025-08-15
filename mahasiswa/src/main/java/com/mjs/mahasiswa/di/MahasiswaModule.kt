@file:Suppress("DEPRECATION")

package com.mjs.mahasiswa.di

import com.mjs.mahasiswa.presentation.MainActivityViewModel
import com.mjs.mahasiswa.presentation.classroom.ClassroomViewModel
import com.mjs.mahasiswa.presentation.home.HomeViewModel
import com.mjs.mahasiswa.presentation.schedule.ScheduleViewModel
import com.mjs.mahasiswa.presentation.setting.SettingViewModel
import com.mjs.mahasiswa.presentation.task.TaskViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mahasiswaModule =
    module {
        viewModel { MainActivityViewModel(get()) }
        viewModel { SettingViewModel(get()) }
        viewModel { HomeViewModel(get()) }
        viewModel { ScheduleViewModel(get()) }
        viewModel { TaskViewModel(get()) }
        viewModel { ClassroomViewModel(get()) }
    }
