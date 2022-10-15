package com.life4.feedz.di.local

import android.content.Context
import androidx.room.Room
import com.life4.feedz.room.news.NewsDatabase
import com.life4.feedz.room.source.SourcesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Singleton
    @Provides
    fun injectRoomDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, SourcesDatabase::class.java, "sources").build()

    @Singleton
    @Provides
    fun injectSourceDao(database: SourcesDatabase) = database.sourceDao()

    @Singleton
    @Provides
    fun injectNewsRoomDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, NewsDatabase::class.java, "news_db").build()

    @Singleton
    @Provides
    fun injectNewsDao(database: NewsDatabase) = database.newsDao()
}