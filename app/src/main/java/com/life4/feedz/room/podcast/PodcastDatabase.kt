package com.life4.feedz.room.podcast

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.life4.feedz.models.room.SavedPodcast
import com.life4.feedz.room.news.NewsConverter

@Database(
    entities = [SavedPodcast::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(NewsConverter::class)
abstract class PodcastDatabase : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
}