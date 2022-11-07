package com.life4.flowpod.di.remote

import com.life4.core.manager.language.MyLanguageManager
import com.life4.flowpod.remote.ApiService
import com.life4.flowpod.remote.FeedzRemoteDataSource
import com.life4.flowpod.remote.source.SourceApiService
import com.life4.flowpod.remote.source.SourceRemoteDataSource
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
    fun provideRemoteDataSourceFeedz(service: ApiService, languageManager: MyLanguageManager) =
        FeedzRemoteDataSource(service, languageManager)

    @Provides
    @Singleton
    fun provideRemoteDataSourceSource(service: SourceApiService) =
        SourceRemoteDataSource(service)
}
