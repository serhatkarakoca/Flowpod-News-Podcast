package com.life4.flowpod.room.podcast

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.life4.flowpod.models.room.FlowPodcast
import com.life4.flowpod.models.room.SavedPodcast
import com.life4.flowpod.room.news.NewsConverter

@Database(
    entities = [SavedPodcast::class, FlowPodcast::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(NewsConverter::class)
abstract class PodcastDatabase : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
}