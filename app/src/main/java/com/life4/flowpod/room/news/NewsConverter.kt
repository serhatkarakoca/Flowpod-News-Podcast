package com.life4.flowpod.room.news

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.life4.flowpod.models.rss_.*

class NewsConverter {
    @TypeConverter
    fun fromStatesHolder(sh: RssPaginationItem): String {
        return Gson().toJson(sh)
    }

    @TypeConverter
    fun toStatesHolder(sh: String): RssPaginationItem {
        return Gson().fromJson(sh, RssPaginationItem::class.java)
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

    @TypeConverter
    fun fromRssResponseHolder(sh: RssResponse?): String? {
        return Gson().toJson(sh)
    }

    @TypeConverter
    fun toRssResponseHolder(sh: String?): RssResponse? {
        return Gson().fromJson(sh, RssResponse::class.java)
    }

    @TypeConverter
    fun fromImageHolder(sh: Image?): String? {
        return Gson().toJson(sh)
    }

    @TypeConverter
    fun toImageHolder(sh: String?): Image? {
        return Gson().fromJson(sh, Image::class.java)
    }
}