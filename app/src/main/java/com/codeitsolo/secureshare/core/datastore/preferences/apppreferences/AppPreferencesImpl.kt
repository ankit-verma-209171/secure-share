package com.codeitsolo.secureshare.core.datastore.preferences.apppreferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

/**
 * Concrete implementation of [AppPreferences]
 */
class AppPreferencesImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AppPreferences {

    companion object {
        private val isFirstTimeLaunchKey = booleanPreferencesKey(name = "is_first_time_launch_key")
    }

    override suspend fun getIsFirstTimeAppLaunch() = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[isFirstTimeLaunchKey] ?: true
    }

    override suspend fun completeFirstTimeAppLaunch() {
        dataStore.edit { preferences ->
            preferences[isFirstTimeLaunchKey] = false
        }
    }
}