package com.life4.feedz.room.news

import androidx.room.TypeConverter
import com.google.gson.Gson

class NewsConverter {
    @TypeConverter
    fun fromStatesHolder(sh: PaginationItems): String {
        return Gson().toJson(sh)
    }

    @TypeConverter
    fun toStatesHolder(sh: String): PaginationItems {
        return Gson().fromJson(sh, PaginationItems::class.java)
    }
}