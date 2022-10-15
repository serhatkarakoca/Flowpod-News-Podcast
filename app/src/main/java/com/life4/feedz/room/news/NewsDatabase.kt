package com.life4.feedz.room.news

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.life4.feedz.models.rss_.RssPaginationItem

@Database(
    entities = [RssPaginationItem::class, NewsRemoteKey::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(NewsConverter::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}