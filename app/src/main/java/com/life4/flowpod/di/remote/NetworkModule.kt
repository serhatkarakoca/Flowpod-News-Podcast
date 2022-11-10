package com.life4.flowpod.di.remote

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.GsonBuilder
import com.life4.core.data.remote.interceptor.HostSelectionInterceptor
import com.life4.core.extensions.DateFormat
import com.life4.flowpod.BuildConfig
import com.life4.flowpod.other.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(
    includes = [
        RemoteDataSourceModule::class,
        RemoteServiceModule::class
    ]
)
object NetworkModule {
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()

    @Provides
    fun provideOkHttpClientBuilder(
        hostSelectionInterceptor: HostSelectionInterceptor
    ): OkHttpClient.Builder = OkHttpClient.Builder()
        .readTimeout(Constant.TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(Constant.TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(hostSelectionInterceptor)

    @Provides
    fun provideConverterFactory(): Converter.Factory = GsonConverterFactory
        .create(
            GsonBuilder()
                .setDateFormat(DateFormat.DEFAULT_DATE_FORMAT.toString())
                .create()
        )

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()

    @Provides
    fun provideHostSelectionInterceptor(): HostSelectionInterceptor = HostSelectionInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        builder: OkHttpClient.Builder,
        interceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addNetworkInterceptor(interceptor)
            builder.addInterceptor(ChuckerInterceptor(context))
        }
        return builder.build()
    }
}
