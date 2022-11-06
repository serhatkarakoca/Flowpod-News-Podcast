package com.life4.feedz.utils

import com.google.gson.Gson
import com.life4.feedz.models.rss_.RssPaginationItem


fun serializeToJson(myClass: RssPaginationItem?): String? {
    val gson = Gson()
    return gson.toJson(myClass)
}


fun deserializeFromJson(jsonString: String?): RssPaginationItem? {
    val gson = Gson()
    return gson.fromJson(jsonString, RssPaginationItem::class.java)
}