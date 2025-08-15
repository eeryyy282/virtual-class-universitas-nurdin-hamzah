@file:Suppress("DEPRECATION")

package com.mjs.detailclass.di

import com.mjs.detailclass.registered.DetailClassRegisteredViewModel
import com.mjs.detailclass.unregistered.DetailClassUnregisteredViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val detailClassModule =
    module {
        viewModel { DetailClassRegisteredViewModel(get()) }
        viewModel { DetailClassUnregisteredViewModel(get()) }
    }
