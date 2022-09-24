package com.life4.feedz.di.remote

import com.life4.feedz.BuildConfig
import com.life4.feedz.remote.ApiService
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
}
