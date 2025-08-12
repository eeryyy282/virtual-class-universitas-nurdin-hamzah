package com.mjs.core.data.source.local.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("AppSettings")

class AppPreference(
    private val dataStore: DataStore<Preferences>,
) {
    private val isLoggedInKey = booleanPreferencesKey("is_logged_in")
    private val loggedInUserIdKey = intPreferencesKey("logged_in_user_id")
    private val loggedInUserTypeKey = stringPreferencesKey("logged_in_user_type")

    private val themeKey = booleanPreferencesKey("theme_setting")

    fun getThemeSetting(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[themeKey] ?: false
        }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[themeKey] = isDarkModeActive
        }
    }

    fun getLoginStatus(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[isLoggedInKey] ?: false
        }

    fun getLoggedInUserId(): Flow<Int?> =
        dataStore.data.map { preferences ->
            preferences[loggedInUserIdKey]
        }

    fun getLoggedInUserType(): Flow<String?> =
        dataStore.data.map { preferences ->
            preferences[loggedInUserTypeKey]
        }

    suspend fun saveLoginSession(
        userId: Int,
        userType: String,
    ) {
        dataStore.edit { preferences ->
            preferences[isLoggedInKey] = true
            preferences[loggedInUserIdKey] = userId
            preferences[loggedInUserTypeKey] = userType
        }
    }

    suspend fun clearLoginSession() {
        dataStore.edit { preferences ->
            preferences[isLoggedInKey] = false
            preferences.remove(loggedInUserIdKey)
            preferences.remove(loggedInUserTypeKey)
        }
    }

    companion object {
        const val USER_TYPE_MAHASISWA = "mahasiswa"
        const val USER_TYPE_DOSEN = "dosen"
    }
}
