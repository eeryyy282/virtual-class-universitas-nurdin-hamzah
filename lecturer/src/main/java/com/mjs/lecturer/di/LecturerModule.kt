package com.mjs.lecturer.di

import com.mjs.lecturer.presentation.MainActivityViewModel
import com.mjs.lecturer.presentation.assignment.AssignmentViewModel
import com.mjs.lecturer.presentation.course.CourseViewModel
import com.mjs.lecturer.presentation.home.HomeViewModel
import com.mjs.lecturer.presentation.schedule.ScheduleViewModel
import com.mjs.lecturer.presentation.setting.SettingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val lecturerModule =
    module {
        viewModel { MainActivityViewModel(get()) }
        viewModel { SettingViewModel(get()) }
        viewModel { HomeViewModel(get()) }
        viewModel { ScheduleViewModel(get()) }
        viewModel { AssignmentViewModel(get()) }
        viewModel { CourseViewModel(get()) }
    }
