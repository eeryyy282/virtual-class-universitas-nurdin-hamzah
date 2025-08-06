package com.mjs.core.di

import androidx.room.Room
import com.mjs.core.data.repository.VirtualClassRepository
import com.mjs.core.data.source.local.LocalDataSource
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.data.source.local.pref.dataStore
import com.mjs.core.data.source.local.room.VirtualClassDatabase
import com.mjs.core.domain.repository.IVirtualClassRepository
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule =
    module {
        val passphrase: ByteArray = SQLiteDatabase.getBytes("virtual_class".toCharArray())
        val factory = SupportFactory(passphrase)

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
                .openHelperFactory(factory)
                .build()
        }
    }

val repositoryModule =
    module {
        single {
            LocalDataSource(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        single { AppPreference(get()) }
        single { androidContext().dataStore }
        single<IVirtualClassRepository> { VirtualClassRepository(get()) }
    }
