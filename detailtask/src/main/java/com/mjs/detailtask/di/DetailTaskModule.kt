package com.mjs.detailtask.di

import com.mjs.detailtask.presentation.DetailTaskViewModel
import com.mjs.detailtask.presentation.submittask.SubmitTaskViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val detailTaskModule =
    module {
        viewModel { DetailTaskViewModel(get()) }
        viewModel { SubmitTaskViewModel(get()) }
    }
