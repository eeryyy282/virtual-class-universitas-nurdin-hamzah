package com.mjs.virtualclassuniversitasnurdinhamzah.di

import com.mjs.core.domain.usecase.pref.ThemeInteractor
import com.mjs.core.domain.usecase.pref.ThemeUseCase
import com.mjs.virtualclassuniversitasnurdinhamzah.presentation.MainActivityViewModel
import com.mjs.virtualclassuniversitasnurdinhamzah.presentation.SplashScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val useCaseModule =
    module {
        factory<ThemeUseCase> { ThemeInteractor(get()) }
    }

val viewModelModule =
    module {
        viewModel { SplashScreenViewModel(get()) }
        viewModel { MainActivityViewModel(get()) }
    }
