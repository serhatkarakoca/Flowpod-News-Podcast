package com.life4.flowpod.di.remote

import com.life4.flowpod.BuildConfig
import com.life4.flowpod.remote.ApiService
import com.life4.flowpod.remote.source.SourceApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RemoteServiceModule {
    @Provides
    @Singleton
    fun provideApiService(
        builder: Retrofit.Builder,
        converterFactory: Converter.Factory,
        client: OkHttpClient
    ): ApiService {
        return builder
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(converterFactory)
            .client(client)
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSourceApiService(
        builder: Retrofit.Builder,
        converterFactory: Converter.Factory,
        client: OkHttpClient
    ): SourceApiService {
        return builder
            .baseUrl(BuildConfig.BASE_URL_GITLAB)
            .addConverterFactory(converterFactory)
            .client(client)
            .build()
            .create(SourceApiService::class.java)
    }
}
