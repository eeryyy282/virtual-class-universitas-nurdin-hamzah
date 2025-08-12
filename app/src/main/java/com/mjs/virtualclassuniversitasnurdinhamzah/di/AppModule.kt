package com.mjs.virtualclassuniversitasnurdinhamzah.di

import com.mjs.core.domain.usecase.virtualclass.VirtualClassInteractor
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import com.mjs.virtualclassuniversitasnurdinhamzah.presentation.SplashScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val useCaseModule =
    module {
        factory<VirtualClassUseCase> { VirtualClassInteractor(get()) }
    }

val viewModelModule =
    module {
        viewModel { SplashScreenViewModel(get()) }
    }
