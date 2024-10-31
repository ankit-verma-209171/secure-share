package com.codeitsolo.secureshare.feature.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Onboarding route to show onboarding screen.
 *
 * @param modifier The Modifier to be applied to this composable
 */
@Composable
fun OnboardingRoute(modifier: Modifier = Modifier) {
    Box(modifier) {
        Text("Onboarding screen")
    }
}

/**
 * A standalone screen to show the onboarding screen.
 *
 * @param modifier The modifier needed to be applied to the composable
 */
@Composable
private fun OnboardingScreen(
    modifier: Modifier = Modifier
) {

}