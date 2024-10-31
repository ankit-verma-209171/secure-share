package com.codeitsolo.secureshare.feature.onboarding.navigation

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.codeitsolo.secureshare.feature.onboarding.OnboardingRoute

/**
 * Represents the onboarding navigation graph
 */
fun NavGraphBuilder.onboardingNavigation() {

    navigation<OnboardingRoute>(
        startDestination = Onboarding,
    ) {

        composable<Onboarding> {
            OnboardingRoute(
                modifier = Modifier
                    .systemBarsPadding()
            )
        }
    }
}
