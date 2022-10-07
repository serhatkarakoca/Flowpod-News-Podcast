package com.life4.feedz.models.source

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sources")
data class SourceModel(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val sourceList: RssFeedResponse? = null
)
