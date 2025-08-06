package com.mjs.core.di

import com.mjs.core.data.repository.Repository
import com.mjs.core.data.repository.ThemeRepository
import com.mjs.core.data.source.local.LocalDataSource
import com.mjs.core.data.source.local.pref.ThemePreference
import com.mjs.core.data.source.local.pref.dataStore
import com.mjs.core.domain.repository.IRepository
import com.mjs.core.domain.repository.IThemeRepository
import com.mjs.core.domain.usecase.VirtualClassUseCase
import com.mjs.core.domain.usecase.attendance.GetAttendanceHistoryUseCase
import com.mjs.core.domain.usecase.attendance.InsertAttendanceUseCase
import com.mjs.core.domain.usecase.auth.GetDosenUseCase
import com.mjs.core.domain.usecase.auth.GetMahasiswaUseCase
import com.mjs.core.domain.usecase.auth.RegisterUseCase
import com.mjs.core.domain.usecase.classroom.EnrollToClassUseCase
import com.mjs.core.domain.usecase.classroom.GetAllKelasUseCase
import com.mjs.core.domain.usecase.classroom.GetEnrolledClassesUseCase
import com.mjs.core.domain.usecase.classroom.GetMaterialsUseCase
import com.mjs.core.domain.usecase.forum.GetForumsUseCase
import com.mjs.core.domain.usecase.forum.GetPostsUseCase
import com.mjs.core.domain.usecase.forum.InsertPostUseCase
import com.mjs.core.domain.usecase.task.GetAssignmentsUseCase
import com.mjs.core.domain.usecase.task.InsertAssignmentUseCase
import com.mjs.core.domain.usecase.task.InsertSubmissionUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule =
    module {
        single { LocalDataSource(get(), get(), get(), get(), get(), get()) }
        single { ThemePreference(get()) }
        single { androidContext().dataStore }
        single<IThemeRepository> { ThemeRepository(get()) }
        single<IRepository> { Repository(get()) }
    }

val useCaseModule =
    module {
        single {
            VirtualClassUseCase(
                getMahasiswaUseCase = GetMahasiswaUseCase(get()),
                getDosenUseCase = GetDosenUseCase(get()),
                registerUseCase = RegisterUseCase(get()),
                getAllKelasUseCase = GetAllKelasUseCase(get()),
                getEnrolledClassesUseCase = GetEnrolledClassesUseCase(get()),
                enrollToClassUseCase = EnrollToClassUseCase(get()),
                getMaterialsUseCase = GetMaterialsUseCase(get()),
                getAssignmentsUseCase = GetAssignmentsUseCase(get()),
                insertAssignmentUseCase = InsertAssignmentUseCase(get()),
                insertSubmissionUseCase = InsertSubmissionUseCase(get()),
                getForumsUseCase = GetForumsUseCase(get()),
                getPostsUseCase = GetPostsUseCase(get()),
                insertPostUseCase = InsertPostUseCase(get()),
                getAttendanceHistoryUseCase = GetAttendanceHistoryUseCase(get()),
                insertAttendanceUseCase = InsertAttendanceUseCase(get()),
            )
        }
    }
