package com.mjs.detailclass.di

import com.mjs.detailclass.registered.DetailClassRegisteredViewModel
import com.mjs.detailclass.unregistered.DetailClassUnregisteredViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val detailClassModule =
    module {
        viewModel {
            DetailClassRegisteredViewModel(get())
            DetailClassUnregisteredViewModel(get())
        }
    }
