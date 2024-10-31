package com.codeitsolo.secureshare.navigation.navigator

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

/**
 * Concrete implementation of [Navigator]
 */
class NavigatorImpl @Inject constructor(
    override val startDestination: Any
) : Navigator {
    private val _navigationActions = Channel<NavigationAction>()
    override val navigationActions = _navigationActions.receiveAsFlow()

    override suspend fun navigate(
        destination: Any,
        navOptions: NavOptionsBuilder.() -> Unit
    ) {
        _navigationActions.send(
            Navigate(
                destination = destination,
                navOptions = navOptions
            )
        )
    }

    override suspend fun navigateUp() {
        _navigationActions.send(NavigateUp)
    }
}