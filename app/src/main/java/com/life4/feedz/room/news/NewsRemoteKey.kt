package com.life4.feedz.room.news

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NewsRemoteKey(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val prev: Int?,
    val next: Int?
)

