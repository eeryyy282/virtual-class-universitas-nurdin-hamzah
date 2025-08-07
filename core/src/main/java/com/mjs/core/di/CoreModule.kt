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
        factory { get<VirtualClassDatabase>().classroomDao() }
        factory { get<VirtualClassDatabase>().taskDao() }
        factory { get<VirtualClassDatabase>().forumDao() }
        factory { get<VirtualClassDatabase>().attendanceDao() }

        single {
            lateinit var dbInstance: VirtualClassDatabase
            val callback = VirtualClassDatabase.PrepopulateCallback { dbInstance.authDao() }
            dbInstance =
                Room
                    .databaseBuilder(
                        androidContext(),
                        VirtualClassDatabase::class.java,
                        "virtual_class.db",
                    ).fallbackToDestructiveMigration(false)
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
                classroomDao = get(),
                taskDao = get(),
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
