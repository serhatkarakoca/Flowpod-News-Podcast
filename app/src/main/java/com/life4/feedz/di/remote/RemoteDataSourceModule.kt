package com.life4.feedz.di.remote

import com.life4.feedz.remote.ApiService
import com.life4.feedz.remote.FeedzRemoteDataSource
import com.life4.feedz.remote.source.SourceApiService
import com.life4.feedz.remote.source.SourceRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RemoteDataSourceModule {
    @Provides
    @Singleton
    fun provideRemoteDataSourceFeedz(service: ApiService) =
        FeedzRemoteDataSource(service)

    @Provides
    @Singleton
    fun provideRemoteDataSourceSource(service: SourceApiService) =
        SourceRemoteDataSource(service)
}
