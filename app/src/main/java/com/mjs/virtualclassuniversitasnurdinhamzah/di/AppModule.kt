package com.mjs.virtualclassuniversitasnurdinhamzah.di

import com.mjs.core.domain.usecase.pref.ThemeInteractor
import com.mjs.core.domain.usecase.pref.ThemeUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory<ThemeUseCase> { ThemeInteractor(get()) }
}