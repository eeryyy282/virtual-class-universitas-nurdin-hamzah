@file:Suppress("DEPRECATION")

package com.mjs.enrollclass.di

import com.mjs.enrollclass.presentation.enrollclass.EnrollClassViewModel
import com.mjs.enrollclass.presentation.enrollrequest.EnrollRequestViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val enrollClassModule =
    module {
        viewModel { EnrollClassViewModel(get()) }
        viewModel { EnrollRequestViewModel(get()) }
    }
