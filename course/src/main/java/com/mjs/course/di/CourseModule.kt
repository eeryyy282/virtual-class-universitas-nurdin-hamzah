@file:Suppress("DEPRECATION")

package com.mjs.course.di

import com.mjs.course.presentation.registered.DetailCourseRegisteredViewModel
import com.mjs.course.presentation.unregistered.DetailCourseUnregisteredViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val courseModule =
    module {
        viewModel { DetailCourseRegisteredViewModel(get()) }
        viewModel { DetailCourseUnregisteredViewModel(get()) }
    }
