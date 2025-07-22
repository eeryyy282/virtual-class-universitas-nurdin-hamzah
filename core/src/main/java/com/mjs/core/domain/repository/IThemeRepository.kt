package com.mjs.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface IThemeRepository {
    fun getThemeSetting(): Flow<Boolean>

    suspend fun saveThemeSetting(isDarkModeActive: Boolean)
}
