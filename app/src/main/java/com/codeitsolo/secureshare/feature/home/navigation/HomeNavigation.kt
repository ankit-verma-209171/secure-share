package com.codeitsolo.secureshare.feature.home.navigation

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.codeitsolo.secureshare.feature.home.HomeRoute

fun NavGraphBuilder.homeNavigation() {

    navigation<HomeRoute>(
        startDestination = Home,
    ) {

        composable<Home> {
            HomeRoute(
                modifier = Modifier
                    .systemBarsPadding()
            )
        }
    }
}
