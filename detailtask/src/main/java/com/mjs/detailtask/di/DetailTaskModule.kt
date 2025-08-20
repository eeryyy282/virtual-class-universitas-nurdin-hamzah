@file:Suppress("DEPRECATION")

package com.mjs.detailtask.di

import com.mjs.detailtask.presentation.DetailTaskViewModel
import com.mjs.detailtask.presentation.edittask.EditTaskViewModel
import com.mjs.detailtask.presentation.submitedtask.SubmittedTaskViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val detailTaskModule =
    module {
        viewModel { DetailTaskViewModel(get()) }
        viewModel { SubmittedTaskViewModel(get()) }
        viewModel { EditTaskViewModel(get()) }
    }
