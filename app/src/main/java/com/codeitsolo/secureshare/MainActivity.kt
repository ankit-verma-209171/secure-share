package com.codeitsolo.secureshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.codeitsolo.secureshare.feature.home.navigation.homeNavigation
import com.codeitsolo.secureshare.feature.onboarding.navigation.onboardingNavigation
import com.codeitsolo.secureshare.navigation.ObserveAsEvents
import com.codeitsolo.secureshare.navigation.navigator.Navigate
import com.codeitsolo.secureshare.navigation.navigator.NavigateUp
import com.codeitsolo.secureshare.navigation.navigator.Navigator
import com.codeitsolo.secureshare.ui.theme.SecureShareTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Entry point of the app
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { mainViewModel.shouldShowSplashScreen() }
        }
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val startDestination by mainViewModel.startDestination.collectAsStateWithLifecycle()

            ObserveAsEvents(flow = navigator.navigationActions) { action ->
                when (action) {
                    is Navigate -> navController.navigate(
                        action.destination
                    ) {
                        action.navOptions(this)
                    }

                    NavigateUp -> navController.navigateUp()
                }
            }

            SecureShareTheme {
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    homeNavigation()
                    onboardingNavigation()
                }
            }
        }
    }

}
