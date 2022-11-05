package com.life4.feedz.models.room


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.life4.feedz.models.rss_.RssPaginationItem

@Entity(tableName = "savedPodcast")
data class SavedPodcast(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val podcastItem: RssPaginationItem?
)
