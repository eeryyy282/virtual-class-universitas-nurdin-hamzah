package com.mjs.core.domain.usecase.pref

import kotlinx.coroutines.flow.Flow

interface ThemeUseCase {
    fun getThemeSetting(): Flow<Boolean>

    suspend fun saveThemeSetting(isDarkModeActive: Boolean)
}
