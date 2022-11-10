package com.life4.flowpod.di.local

import android.content.Context
import androidx.room.Room
import com.life4.flowpod.exoplayer.MusicSource
import com.life4.flowpod.exoplayer.service.MusicServiceConnection
import com.life4.flowpod.room.news.NewsDatabase
import com.life4.flowpod.room.podcast.PodcastDatabase
import com.life4.flowpod.room.source.SourcesDatabase
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

    @Singleton
    @Provides
    fun injectPodcastRoomDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, PodcastDatabase::class.java, "podcast_db").build()

    @Singleton
    @Provides
    fun injectPodcastDao(database: PodcastDatabase) = database.podcastDao()

    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context,
        mediaSource: MusicSource
    ) = MusicServiceConnection(context, mediaSource)
}