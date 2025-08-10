@file:Suppress("DEPRECATION")

package com.mjs.profilesettings.di

import com.mjs.profilesettings.presentation.ProfileSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val profileSettingsModule =
    module {
        viewModel {
            ProfileSettingsViewModel(get(), get())
        }
    }
