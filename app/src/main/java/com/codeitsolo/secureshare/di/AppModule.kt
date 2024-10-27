package com.codeitsolo.secureshare.di

import com.codeitsolo.secureshare.feature.home.navigation.HomeRoute
import com.codeitsolo.secureshare.navigation.navigator.Navigator
import com.codeitsolo.secureshare.navigation.navigator.NavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNavigator(): Navigator {
        return NavigatorImpl(
            startDestination = HomeRoute
        )
    }
}