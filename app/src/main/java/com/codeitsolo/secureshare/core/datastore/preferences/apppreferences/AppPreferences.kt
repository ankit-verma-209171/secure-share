package com.codeitsolo.secureshare.core.datastore.preferences.apppreferences

import kotlinx.coroutines.flow.Flow

/**
 * Represents the app preferences.
 */
interface AppPreferences {


    /**
     * Gets if user is opening app for the first time
     *
     * @return The flow of First Time App Launch Updates
     */
    suspend fun getIsFirstTimeAppLaunch(): Flow<Boolean>

    /**
     * Completes the first time app launch
     */
    suspend fun completeFirstTimeAppLaunch()
}