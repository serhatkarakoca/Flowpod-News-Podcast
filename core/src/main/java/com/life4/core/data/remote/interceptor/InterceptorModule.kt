package com.life4.core.data.remote.interceptor

import com.life4.core.manager.storage.MyStorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object InterceptorModule {
    @Singleton
    @Provides
    fun provideAuthInterceptor(myStorageManager: MyStorageManager) = AuthInterceptor(myStorageManager)
}
