package com.codeitsolo.secureshare.navigation.navigator

import androidx.navigation.NavOptionsBuilder
import com.codeitsolo.secureshare.navigation.Destination
import kotlinx.coroutines.flow.Flow

interface Navigator {
    val startDestination: Destination
    val navigationActions: Flow<NavigationAction>

    suspend fun navigate(
        destination: Destination,
        navOptions: NavOptionsBuilder.() -> Unit = {}
    )

    suspend fun navigateUp()
}
