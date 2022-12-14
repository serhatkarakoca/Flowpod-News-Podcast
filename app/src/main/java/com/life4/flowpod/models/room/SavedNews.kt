package com.life4.flowpod.models.room


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.life4.flowpod.models.rss_.RssPaginationItem

@Entity(tableName = "savedNews")
data class SavedNews(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val newsItem: RssPaginationItem?
)
