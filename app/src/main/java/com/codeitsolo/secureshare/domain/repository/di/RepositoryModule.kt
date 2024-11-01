package com.codeitsolo.secureshare.domain.repository.di

import com.codeitsolo.secureshare.domain.repository.mediastore.MediaStoreRepository
import com.codeitsolo.secureshare.domain.repository.mediastore.MediaStoreRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository module
 */
@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindMediaStoreRepository(impl: MediaStoreRepositoryImpl): MediaStoreRepository
}
