package com.life4.flowpod.room.news

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.life4.flowpod.models.room.NewsRemoteKey
import com.life4.flowpod.models.room.SavedNews
import com.life4.flowpod.models.rss_.RssPaginationItem

@Database(
    entities = [RssPaginationItem::class, NewsRemoteKey::class, SavedNews::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(NewsConverter::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}