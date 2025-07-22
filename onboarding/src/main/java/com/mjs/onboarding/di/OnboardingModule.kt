package com.mjs.onboarding.di

import com.mjs.onboarding.ui.OnBoardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingModule = module {
    viewModel { OnBoardingViewModel(get()) }
}