package com.life4.flowpod.di

import com.life4.flowpod.remote.FeedzRemoteDataSource
import com.life4.flowpod.remote.FeedzRepository
import com.life4.flowpod.remote.source.SourceRemoteDataSource
import com.life4.flowpod.remote.source.SourceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    fun provideRepositoryFeedz(
        remoteDataSource: FeedzRemoteDataSource
    ) = FeedzRepository(remoteDataSource)

    @Provides
    fun provideRepositorySource(
        remoteDataSource: SourceRemoteDataSource
    ) = SourceRepository(remoteDataSource)
}
