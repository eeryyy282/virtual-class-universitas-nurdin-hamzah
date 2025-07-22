package com.mjs.core.data.source.local

import com.mjs.core.data.source.local.pref.ThemePreference
import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val themePreference: ThemePreference,
) {
    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        themePreference.saveThemeSetting(isDarkModeActive)
    }

    fun getThemeSetting(): Flow<Boolean> = themePreference.getThemeSetting()
}
