package com.life4.feedz.room.news

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.life4.feedz.models.rss_.Enclosure
import com.life4.feedz.models.rss_.Itunes

class NewsConverter {
    @TypeConverter
    fun fromStatesHolder(sh: PaginationItems): String {
        return Gson().toJson(sh)
    }

    @TypeConverter
    fun toStatesHolder(sh: String): PaginationItems {
        return Gson().fromJson(sh, PaginationItems::class.java)
    }

    @TypeConverter
    fun fromEnclosuresHolder(sh: Enclosure?): String? {
        return Gson().toJson(sh)
    }

    @TypeConverter
    fun toEnclosureHolder(sh: String?): Enclosure? {
        return Gson().fromJson(sh, Enclosure::class.java)
    }

    @TypeConverter
    fun fromItunesHolder(sh: Itunes?): String? {
        return Gson().toJson(sh)
    }

    @TypeConverter
    fun toItunesHolder(sh: String?): Itunes? {
        return Gson().fromJson(sh, Itunes::class.java)
    }
}