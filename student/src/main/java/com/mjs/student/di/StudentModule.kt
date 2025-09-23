@file:Suppress("DEPRECATION")

package com.mjs.student.di

import com.mjs.student.presentation.MainActivityViewModel
import com.mjs.student.presentation.assignment.AssignmentViewModel
import com.mjs.student.presentation.course.CourseViewModel
import com.mjs.student.presentation.home.HomeViewModel
import com.mjs.student.presentation.schedule.ScheduleViewModel
import com.mjs.student.presentation.setting.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val studentModule =
    module {
        viewModel { MainActivityViewModel(get()) }
        viewModel { SettingViewModel(get()) }
        viewModel { HomeViewModel(get()) }
        viewModel { ScheduleViewModel(get()) }
        viewModel { AssignmentViewModel(get()) }
        viewModel { CourseViewModel(get()) }
    }
