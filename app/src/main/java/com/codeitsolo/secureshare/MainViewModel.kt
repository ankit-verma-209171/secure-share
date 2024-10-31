package com.codeitsolo.secureshare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeitsolo.secureshare.core.common.dispatcher.AppDispatchers
import com.codeitsolo.secureshare.core.common.dispatcher.Dispatcher
import com.codeitsolo.secureshare.core.datastore.preferences.apppreferences.AppPreferences
import com.codeitsolo.secureshare.feature.home.navigation.HomeRoute
import com.codeitsolo.secureshare.feature.onboarding.navigation.OnboardingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents main view model for [SecureShareApp]
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val appPreferences: AppPreferences,
) : ViewModel() {

    val startDestination: MutableStateFlow<Any> = MutableStateFlow(OnboardingRoute)
    private var _isLoading = true

    init {
        viewModelScope.launch(ioDispatcher) {
            val isFirstTimeLaunch = appPreferences.getIsFirstTimeAppLaunch().first()
            if (isFirstTimeLaunch) {
                startDestination.update { OnboardingRoute }
                appPreferences.completeFirstTimeAppLaunch()
            } else {
                startDestination.update { HomeRoute }
            }
            delay(SPLASH_SCREEN_TIMEOUT)
            _isLoading = false
        }
    }

    fun shouldShowSplashScreen(): Boolean = _isLoading

    companion object {
        private const val SPLASH_SCREEN_TIMEOUT = 800L
    }
}
