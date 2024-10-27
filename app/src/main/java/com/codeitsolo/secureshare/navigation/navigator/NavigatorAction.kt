package com.codeitsolo.secureshare.navigation.navigator

import androidx.navigation.NavOptionsBuilder
import com.codeitsolo.secureshare.navigation.Destination

sealed interface NavigationAction

data class Navigate(
    val destination: Destination,
    val navOptions: NavOptionsBuilder.() -> Unit = {}
) : NavigationAction

data object NavigateUp : NavigationAction
