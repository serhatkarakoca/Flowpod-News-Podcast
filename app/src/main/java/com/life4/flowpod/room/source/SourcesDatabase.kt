package com.life4.flowpod.room.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.life4.flowpod.models.source.SourceModel

@Database(entities = [SourceModel::class], version = 1, exportSchema = false)
@TypeConverters(SourceConverter::class)
abstract class SourcesDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
}