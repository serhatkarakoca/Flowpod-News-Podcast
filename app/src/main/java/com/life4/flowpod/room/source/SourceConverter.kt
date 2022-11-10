package com.life4.flowpod.room.source

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.life4.flowpod.models.source.RssFeedResponse

class SourceConverter {
    @TypeConverter
    fun fromStatesHolder(sh: RssFeedResponse): String {
        return Gson().toJson(sh)
    }

    @TypeConverter
    fun toStatesHolder(sh: String): RssFeedResponse {
        return Gson().fromJson(sh, RssFeedResponse::class.java)
    }
}