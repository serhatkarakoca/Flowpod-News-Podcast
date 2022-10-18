package com.life4.feedz.room.news

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.life4.feedz.models.rss_.RssPaginationItem

class NewsConverter {
    @TypeConverter
    fun fromStatesHolder(sh: RssPaginationItem): String {
        return Gson().toJson(sh)
    }

    @TypeConverter
    fun toStatesHolder(sh: String): RssPaginationItem {
        return Gson().fromJson(sh, RssPaginationItem::class.java)
    }
}