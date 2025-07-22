package com.mjs.core.domain.usecase.pref

import com.mjs.core.domain.repository.IThemeRepository
import kotlinx.coroutines.flow.Flow

class ThemeInteractor(private val themeRepository: IThemeRepository) : ThemeUseCase {
    override fun getThemeSetting(): Flow<Boolean> =
        themeRepository.getThemeSetting()

    override suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        themeRepository.saveThemeSetting(isDarkModeActive)
    }
}