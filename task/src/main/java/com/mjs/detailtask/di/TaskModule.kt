@file:Suppress("DEPRECATION")

package com.mjs.detailtask.di

import com.mjs.detailtask.presentation.detailtask.DetailTaskViewModel
import com.mjs.detailtask.presentation.edittask.EditTaskViewModel
import com.mjs.detailtask.presentation.submitedtask.SubmittedTaskViewModel
import com.mjs.detailtask.presentation.submitedtask.detailsubmitedtask.DetailSubmittedTaskViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val taskModule =
    module {
        viewModel { DetailTaskViewModel(get()) }
        viewModel { SubmittedTaskViewModel(get()) }
        viewModel { EditTaskViewModel(get()) }
        viewModel { DetailSubmittedTaskViewModel(get()) }
    }
