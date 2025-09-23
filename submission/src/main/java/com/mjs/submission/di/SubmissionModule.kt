package com.mjs.submission.di

import com.mjs.submission.presentation.detailsubmission.DetailSubmissionViewModel
import com.mjs.submission.presentation.listsubmission.ListSubmissionViewModel
import com.mjs.submission.presentation.submitassignment.SubmitAssignmentViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val submissionModule =
    module {
        viewModel { DetailSubmissionViewModel(get()) }
        viewModel { ListSubmissionViewModel(get()) }
        viewModel { SubmitAssignmentViewModel(get()) }
    }
