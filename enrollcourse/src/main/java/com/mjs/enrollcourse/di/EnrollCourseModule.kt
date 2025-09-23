@file:Suppress("DEPRECATION")

package com.mjs.enrollcourse.di

import com.mjs.enrollcourse.presentation.enrollcourse.EnrollCourseViewModel
import com.mjs.enrollcourse.presentation.enrollrequest.EnrollRequestViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val enrollCourseModule =
    module {
        viewModel { EnrollCourseViewModel(get()) }
        viewModel { EnrollRequestViewModel(androidApplication(), get()) }
    }
