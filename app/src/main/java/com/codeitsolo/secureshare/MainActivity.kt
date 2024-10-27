package com.codeitsolo.secureshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.codeitsolo.secureshare.feature.home.navigation.HomeRoute
import com.codeitsolo.secureshare.feature.home.navigation.homeNavigation
import com.codeitsolo.secureshare.navigation.ObserveAsEvents
import com.codeitsolo.secureshare.navigation.navigator.Navigate
import com.codeitsolo.secureshare.navigation.navigator.NavigateUp
import com.codeitsolo.secureshare.navigation.navigator.Navigator
import com.codeitsolo.secureshare.ui.theme.SecureShareTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ObserveAsEvents(flow = navigator.navigationActions) { action ->
                when(action) {
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
                    startDestination = HomeRoute
                ) {
                    homeNavigation()
                }
            }
        }
    }
}
