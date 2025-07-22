package com.mjs.core.di

import com.mjs.core.data.repository.ThemeRepository
import com.mjs.core.data.source.local.LocalDataSource
import com.mjs.core.data.source.local.pref.ThemePreference
import com.mjs.core.data.source.local.pref.dataStore
import com.mjs.core.domain.repository.IThemeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule =
    module {
        single { LocalDataSource(get()) }
        single { ThemePreference(get()) }
        single { androidContext().dataStore }
        single<IThemeRepository> { ThemeRepository(get()) }
    }
