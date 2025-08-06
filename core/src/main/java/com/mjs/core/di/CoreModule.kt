package com.mjs.core.di

import androidx.room.Room
import com.mjs.core.data.repository.ThemeRepository
import com.mjs.core.data.repository.VirtualClassRepository
import com.mjs.core.data.source.local.LocalDataSource
import com.mjs.core.data.source.local.pref.ThemePreference
import com.mjs.core.data.source.local.pref.dataStore
import com.mjs.core.data.source.local.room.VirtualClassDatabase
import com.mjs.core.domain.repository.IThemeRepository
import com.mjs.core.domain.repository.IVirtualClassRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule =
    module {
        factory { get<VirtualClassDatabase>().authDao() }
        factory { get<VirtualClassDatabase>().classroomDao() }
        factory { get<VirtualClassDatabase>().taskDao() }
        factory { get<VirtualClassDatabase>().forumDao() }
        factory { get<VirtualClassDatabase>().attendanceDao() }
        single {
            Room
                .databaseBuilder(
                    androidContext(),
                    VirtualClassDatabase::class.java,
                    "virtual_class.db",
                ).fallbackToDestructiveMigration(false)
                .build()
        }
    }
val repositoryModule =
    module {
        single { LocalDataSource(get(), get(), get(), get(), get(), get()) }
        single { ThemePreference(get()) }
        single { androidContext().dataStore }
        single<IThemeRepository> { ThemeRepository(get()) }
        single<IVirtualClassRepository> { VirtualClassRepository(get()) }
    }
