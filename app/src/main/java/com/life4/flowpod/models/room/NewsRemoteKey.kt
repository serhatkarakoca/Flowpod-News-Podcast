package com.life4.flowpod.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NewsRemoteKey(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val prev: Int?,
    val next: Int?
)

