package com.life4.feedz.models.room


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.life4.feedz.models.rss_.RssResponse

@Entity(tableName = "flowPodcast")
data class FlowPodcast(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val podcastItem: RssResponse?
)
