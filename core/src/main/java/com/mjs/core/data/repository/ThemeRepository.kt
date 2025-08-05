package com.mjs.core.data.repository

import com.mjs.core.data.source.local.LocalDataSource
import com.mjs.core.domain.repository.IThemeRepository
import kotlinx.coroutines.flow.Flow

class ThemeRepository(
    private val localDataSource: LocalDataSource,
) : IThemeRepository {
    override fun getThemeSetting(): Flow<Boolean> = localDataSource.getThemeSetting()

    override suspend fun saveThemeSetting(isDarkModeActive: Boolean) = localDataSource.saveThemeSetting(isDarkModeActive)
}
