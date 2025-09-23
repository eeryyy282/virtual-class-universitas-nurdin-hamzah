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
        val sqlCipherFactory = SupportFactory(passphrase)

        factory { get<VirtualClassDatabase>().authDao() }
        factory { get<VirtualClassDatabase>().courseDao() }
        factory { get<VirtualClassDatabase>().assignmentDao() }
        factory { get<VirtualClassDatabase>().submissionDao() }
        factory { get<VirtualClassDatabase>().forumDao() }
        factory { get<VirtualClassDatabase>().attendanceDao() }

        single {
            lateinit var dbInstance: VirtualClassDatabase
            val callback = VirtualClassDatabase.PrepopulateCallback { dbInstance }
            dbInstance =
                Room
                    .databaseBuilder(
                        androidContext(),
                        VirtualClassDatabase::class.java,
                        "virtual_class.db",
                    ).fallbackToDestructiveMigration(true)
                    .openHelperFactory(sqlCipherFactory)
                    .addCallback(callback)
                    .build()
            dbInstance
        }
    }

val repositoryModule =
    module {
        single {
            LocalDataSource(
                appPreference = get(),
                authDao = get(),
                courseDao = get(),
                assignmentDao = get(),
                submissionDao = get(),
                forumDao = get(),
                attendanceDao = get(),
            )
        }
        single {
            AppPreference(
                dataStore = get(),
            )
        }
        single { androidContext().dataStore }
        single<IVirtualClassRepository> {
            VirtualClassRepository(
                localDataSource = get(),
            )
        }
    }
