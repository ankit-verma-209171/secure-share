package com.codeitsolo.secureshare.di

import com.codeitsolo.secureshare.feature.onboarding.navigation.OnboardingRoute
import com.codeitsolo.secureshare.navigation.navigator.Navigator
import com.codeitsolo.secureshare.navigation.navigator.NavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * App Module
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNavigator(): Navigator {
        return NavigatorImpl(
            startDestination = OnboardingRoute
        )
    }
}