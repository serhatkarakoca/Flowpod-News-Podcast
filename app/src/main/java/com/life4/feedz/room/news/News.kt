package com.life4.feedz.room.news


import androidx.room.PrimaryKey
import com.life4.feedz.models.rss_.RssPaginationItem


data class News(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val newsItem: RssPaginationItem
)
