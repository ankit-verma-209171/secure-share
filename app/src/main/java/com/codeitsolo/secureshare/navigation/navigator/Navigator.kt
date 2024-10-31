package com.codeitsolo.secureshare.navigation.navigator

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.flow.Flow

/**
 * Represents a navigator
 */
interface Navigator {

    /**
     * Start destination of the navigator
     */
    val startDestination: Any

    /**
     * Flow of navigation actions
     */
    val navigationActions: Flow<NavigationAction>

    /**
     * Navigates to a destination
     *
     * @param destination Destination to navigate to
     * @param navOptions Navigation options
     */
    suspend fun navigate(
        destination: Any,
        navOptions: NavOptionsBuilder.() -> Unit = {}
    )

    /**
     * Navigates up
     */
    suspend fun navigateUp()
}
