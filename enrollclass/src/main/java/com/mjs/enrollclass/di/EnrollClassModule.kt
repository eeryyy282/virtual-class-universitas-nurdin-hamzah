package com.mjs.enrollclass.di

import com.mjs.enrollclass.presentation.EnrollClassViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val enrollClassModule =
    module {
        viewModel {
            EnrollClassViewModel(get())
        }
    }
