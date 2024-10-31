package com.codeitsolo.secureshare.navigation.navigator

import androidx.navigation.NavOptionsBuilder

/**
 * Represents a navigation action
 */
sealed interface NavigationAction

/**
 * Navigates to a destination
 */
data class Navigate(
    val destination: Any,
    val navOptions: NavOptionsBuilder.() -> Unit = {}
) : NavigationAction

/**
 * Navigates up
 */
data object NavigateUp : NavigationAction
