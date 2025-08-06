package com.mjs.virtualclassuniversitasnurdinhamzah

import android.app.Application
import com.mjs.core.di.databaseModule
import com.mjs.core.di.repositoryModule
import com.mjs.virtualclassuniversitasnurdinhamzah.di.useCaseModule
import com.mjs.virtualclassuniversitasnurdinhamzah.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    useCaseModule,
                    viewModelModule,
                    databaseModule,
                    repositoryModule,
                ),
            )
        }
    }
}
