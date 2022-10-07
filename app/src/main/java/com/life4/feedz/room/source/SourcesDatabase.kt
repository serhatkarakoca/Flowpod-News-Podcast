package com.life4.feedz.room.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.life4.feedz.models.source.SourceModel

@Database(entities = [SourceModel::class], version = 1, exportSchema = false)
@TypeConverters(SourceConverter::class)
abstract class SourcesDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
}