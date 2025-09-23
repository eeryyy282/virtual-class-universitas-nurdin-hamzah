@file:Suppress("DEPRECATION")

package com.mjs.assignment.di

import com.mjs.assignment.presentation.detailassignment.DetailAssignmentViewModel
import com.mjs.assignment.presentation.editassignment.EditAssignmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val assignmentModule =
    module {
        viewModel { DetailAssignmentViewModel(get()) }
        viewModel { EditAssignmentViewModel(get()) }
    }
